package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.service.ObservationService;
import com.vd.backend.service.ProfilesService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class ObservationServiceImpl implements ObservationService {

    private Map<String, String> typeMap = Map.of(
            "Body-Weight","weight",
            "Body-Height", "height",
            "Body-Blood", "blood",
            "Body-Pressure", "pressure"
    );

    private JSONObject observationTemplate;


    @Autowired
    private ProfilesService profilesService;

    /**
     * Formatting by time
     * @param data
     * @return
     */
    @Override
    public String formatBundle(String data) {

        JSONArray rel = new JSONArray();

        JSONArray entries = JSON.parseObject(data).getJSONArray("entry");

        // time -> (height, weight, blood, heart)
        Map<String, JSONObject> timeBodyMap = new HashMap<>();

        for (int i=0; entries != null && i < entries.size(); i++) {

            String effectiveDateTime = null, type = null;
            Double value;

            try {
                JSONObject resource = (JSONObject) entries.getJSONObject(i).get("resource");
                JSONObject typeInfo = (JSONObject) resource
                        .getJSONObject("code")
                        .getJSONArray("coding").get(0);
                type = typeMap.get(typeInfo.getString("display"));
                effectiveDateTime = resource.getString("effectiveDateTime");
                value = resource.getJSONObject("valueQuantity").getDoubleValue("value");

            } catch (Exception e) {
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
    public String formatToObservation(String resource, String id, String data) {

        JSONObject inputData = JSON.parseObject(data);

        log.info("add observation {} (before formatting)", inputData.toString());

        List<String> rels = new ArrayList<>();

        typeMap.values().forEach(val -> {
            try {
                // make fhir resource data
                observationTemplate.getJSONObject("subject").put("reference", resource + "/" + id);
                observationTemplate.getJSONObject("valueQuantity").put("value", inputData.getJSONObject(val).get("value"));
                observationTemplate.getJSONObject("valueQuantity").put("unit", (String) inputData.getJSONObject(val).get("unit"));
                observationTemplate.put("effectiveDateTime", (String) inputData.get("effectiveDateTime"));

            } catch (Exception e) {
                e.printStackTrace();
                log.error("Data field incorrect");
            }

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
        JSONObject codingObject = new JSONObject();
        codingObject.put("system", "http://loinc.org");
        codingObject.put("code", "29463-7");
        codingObject.put("display", "N/A");                         // Should be changed
        codeObject.put("coding", codingObject);
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
