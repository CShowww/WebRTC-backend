package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.service.CacheService;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.service.ObservationService;
import com.vd.backend.service.ProfilesService;
import com.vd.backend.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ObservationServiceImpl implements ObservationService {

    private Map<String, String> typeMap = Map.of(
            "Body-Weight","weight",
            "Body-Height", "height",
            "Body-Blood", "blood",
            "Body-Heart", "heart"
    );

    private JSONObject observationTemplate;


    @Autowired
    private ProfilesService profilesService;

    @Autowired
    private CacheService cacheService;




    /**
     * Formatting by time
     * @param data
     * @return
     */
    @Override
    public String getSummary(String data) {

        JSONArray rel = new JSONArray();

        JSONArray resources = JSON.parseArray(data);

        // time -> (height, weight, blood, heart)
        Map<String, JSONObject> timeBodyMap = new HashMap<>();

        for (int i=0; resources != null && i < resources.size(); i++) {

            String effectiveDateTime = null, type = null;
            Double value;

            try {
                JSONObject resource = (JSONObject) resources.getJSONObject(i);
                JSONObject typeInfo = (JSONObject) resource
                        .getJSONObject("code")
                        .getJSONArray("coding").get(0);
                type = typeMap.get(typeInfo.getString("display"));
                effectiveDateTime = resource.getString("effectiveDateTime");
                value = resource.getJSONObject("valueQuantity").getDoubleValue("value");


                log.info("resource: {}, type: {}", resource.toJSONString(), type);


            } catch (Exception e) {
                e.printStackTrace();
                log.error("The observation {} is not supported for visuliation", i);
                continue;
            }

            /**
             * if already contains data in that day, update its value
             * if not, init empty value
             */
            if (timeBodyMap.containsKey(effectiveDateTime)) {

                JSONObject values = timeBodyMap.get(effectiveDateTime);

                values.put(type, value);

                timeBodyMap.put(effectiveDateTime, values);

            } else {
                JSONObject values = new JSONObject();

                typeMap.values().forEach(e -> {
                    values.put(e, 0.0);
                });

                values.put(type, value);
                timeBodyMap.put(effectiveDateTime, values);
            }

        }

        // transfer to required format
        timeBodyMap.keySet().forEach(k -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(timeBodyMap.get(k));
            jsonObject.put("effectiveDateTime", k);
            rel.add(jsonObject);
        });

        return rel.toString();
    }

    /**
     * Formatting frontend post to fhir standard.
     * Frontend request contain four observation
     * @param data
     * @return
     */
    @Override
    public String addObservation(String resource, String id, String data) {

        JSONObject inputData = JSON.parseObject(data);

        log.info("add observation {} (before formatting)", inputData.toString());

        List<String> rels = new ArrayList<>();

        typeMap.values().forEach(type -> {
            try {

                String ref = resource + "/" + id;
                String effectiveDateTime = inputData.getString("effectiveDateTime");
                String formattedType = typeMap.keySet().stream().filter(k -> {
                    return typeMap.get(k).equals(type);
                }).collect(Collectors.toList()).get(0);


                // make fhir resource data
                observationTemplate.getJSONObject("subject").put("reference", ref);
                observationTemplate.getJSONObject("valueQuantity").put("value", inputData.getJSONObject(type).get("value"));
                observationTemplate.getJSONObject("valueQuantity").put("unit", (String) inputData.getJSONObject(type).get("unit"));

                observationTemplate.put("code", JsonUtil.getFakeCode(formattedType));

                observationTemplate.put("effectiveDateTime", effectiveDateTime);



                // remove duplicate from cache
                Map<String, String> observations = cacheService.getValueByPrefix("Observation");

                List<String> removes = new ArrayList<>();

                observations.entrySet().forEach(entry -> {
                    String key = entry.getKey();

                    JSONObject valObj = JSON.parseObject(entry.getValue());

                    if (ref.equals(valObj.getJSONObject("subject").getString("reference"))
                            && effectiveDateTime.equals(valObj.getString("effectiveDateTime"))
                    && formattedType.equals(valObj.getJSONObject("code").getJSONArray("coding").getJSONObject(0).getString("display"))) {

                        cacheService.del(key);

                        removes.add(valObj.getString("id"));
                    }
                });

                // remove duplicate from fhir server
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (String id : removes) {
                            profilesService.delete("Observation", id);
                        }
                    }
                });

                t.start();


            } catch (Exception e) {
                e.printStackTrace();
                log.error("Data field incorrect");
            }



            // Add updated data to cache and fhir server
            try {
                // send to fhir server
                String fhirData = observationTemplate.toString();

                String rel = profilesService.add("Observation", fhirData).getData();

                rels.add(rel);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return rels.toString();
    }







    /**
     * An Observation resource template which can be used to
     * format data from frontend
     */
    @PostConstruct
    public void init() {
        observationTemplate = new JSONObject();

        observationTemplate.put("resourceType", "Observation");
        observationTemplate.put("status", "final");

        JSONObject codeObject = new JSONObject();
        JSONArray codingArray = new JSONArray();
        JSONObject element = new JSONObject();

        element.put("system", "http://loinc.org");
        element.put("code", "29463-7");
        element.put("display", "N/A");                           // Should be changed
        codingArray.add(element);
        codeObject.put("coding", codingArray);

        observationTemplate.put("code", codeObject);


        JSONObject subjectObject = new JSONObject();
        subjectObject.put("reference", "N/A");                      // Should be changed
        observationTemplate.put("subject", subjectObject);

        observationTemplate.put("effectiveDateTime", "N/A");        // Should be changed

        JSONObject valueQuantityObject = new JSONObject();          // Should be changed
        valueQuantityObject.put("value", -1);
        valueQuantityObject.put("unit", "N/A");
        observationTemplate.put("valueQuantity", valueQuantityObject);
    }



}
