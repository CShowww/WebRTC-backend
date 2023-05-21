package com.vd.backend.controller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.RemoteFhirService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.alibaba.fastjson.JSON;


/**
 *  Get raw fhir data
 */

@Slf4j
@RestController
@RequestMapping("/fhir")
@CrossOrigin
public class FhirController {
    @Autowired
    private RemoteFhirService remoteFhirService;

    /**
     *  profiles: return fhir resources
     */
    @GetMapping("/profiles/{resource}/{id}")
    public R<String> get(@PathVariable String id, @PathVariable String resource, HttpServletRequest request) {
        log.info("Get {}/{}", resource, id);

        String rel = "";
        try{
            rel = remoteFhirService.get(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }
        return R.success(rel);
    }


    @GetMapping("/profiles/{resource}")
    public R<String> getAll(@PathVariable String resource) {
        log.info("Get all {}", resource);
        String rel = "";

        try {
            rel = remoteFhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }

        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();

        for (int i = 0; i < entry.size(); i++) {
            JSONObject jsonObject = new JSONObject();

            String family = "", phone = "", given = "";

            JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
            JSONObject name = res.getJSONArray("name")
                    .getJSONObject(0);
            String id = res.getString("id");


            family = name.getString("family");
            for (Object s: name.getJSONArray("given")) {
                given += (String) s + " ";
            }

            JSONArray telecom = res.getJSONArray("telecom");
            for (int j = 0; j < telecom.size(); j++) {
                JSONObject t = telecom.getJSONObject(j);
                String system = t.getString("system");
                if ("phone".equals(system)) {
                    phone = t.getString("value");
                }
            }
            jsonObject.put("family", family);
            jsonObject.put("given", given);
            jsonObject.put("contact", phone);
            jsonObject.put("id", id);

            ans.add(jsonObject);
        }


        return R.success(ans.toString());
    }


    @PutMapping("/profiles/{resource}/{id}")
    public R<String> update(@PathVariable String resource, @PathVariable String id, @RequestBody String data) {
        log.info("Update {}/{}", resource, id);
        String rel = "";
        try {
            rel = remoteFhirService.update(resource, id, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("UPDATE fail");
        }
        return R.success(rel);
    }

    @DeleteMapping("/profiles/{resource}/{id}")
    public R<String> delete(@PathVariable String resource, @PathVariable String id) {
        log.info("Delete {}/{}", resource, id);
        String rel = "";
        try {
            rel = remoteFhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("DELETE fail");
        }

        return R.success(rel);
    }

    @PostMapping ("/profiles/{resource}")
    public R<String> add(@PathVariable String resource, @RequestBody String data) {
        log.info("Post {}", resource);
        String rel = "";
        try {
            rel = remoteFhirService.add(resource, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }

        JSONObject jsonObject = JSONObject.parseObject(rel);
        log.info(jsonObject.toString() +" " + jsonObject.getObject("id", String.class));
        String pId = jsonObject.getString("id");
        R<String> r = R.success(rel);
        r.setMsg(pId);
        return r;
    }

    /**
     *  health summary (tmp)
     */

    // display和download summary是一个接口
    @GetMapping("/observation/{resource}/{id}")
    public R<String> getSummary(@PathVariable String resource, @PathVariable String id) {

        // 1. gather all observation
        String bundle = remoteFhirService.getBySubject("Patient/"+id);

        // 2. filter Observation
        JSONArray entries = JSON.parseObject(bundle).getJSONArray("entry");

        JSONObject healthDataCSV = new JSONObject();

        JSONArray weightArray = new JSONArray();
        JSONArray heightArray = new JSONArray();

        for (int i = 0; i < entries.size(); i++) {
            JSONObject res = (JSONObject) entries.getJSONObject(i)
                    .getJSONObject("resource");

            JSONObject obj = (JSONObject) res
                    .getJSONObject("code")
                    .getJSONArray("coding").get(0);

            if (obj.get("display").equals("Body-Weight")) {
                JSONObject weightDatum = new JSONObject();
                weightDatum.put("effectiveDateTime", res.getString("effectiveDateTime"));
                weightDatum.put("value", res.getJSONObject("valueQuantity").getDouble("value"));
                weightDatum.put("notes", "any notes"); // 添加你需要的字段
                weightArray.add(weightDatum);
            } else if (obj.get("display").equals("Body-Height")) {
                JSONObject heightDatum = new JSONObject();
                heightDatum.put("effectiveDateTime", res.getString("effectiveDateTime"));
                heightDatum.put("value", res.getJSONObject("valueQuantity").getDouble("value"));
                heightDatum.put("notes", "any notes"); // 添加你需要的字段
                heightArray.add(heightDatum);
            }
        }

        healthDataCSV.put("weight", weightArray);
        healthDataCSV.put("height", heightArray);


        return R.success(healthDataCSV.toString());
    }

    @PostMapping("/observation/{resource}/{id}")
    public R<String> addObservation(@PathVariable String resource, @PathVariable String id, @RequestBody String data) throws IOException {
        String weightObservation = "", heightObservation = "";

        JSONObject templates = JSON.parseObject("{\n" +
                "  \"resourceType\": \"Observation\",\n" +
                "  \"status\": \"final\",\n" +
                "  \"code\": {\n" +
                "    \"coding\": [\n" +
                "      {\n" +
                "        \"system\": \"http://loinc.org\",\n" +
                "        \"code\": \"29463-7\",\n" +
                "        \"display\": \"Body-Weight\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"subject\": {\n" +
                "    \"reference\": \"Patient/1326\"\n" +
                "  },\n" +
                "\n" +
                "  \"effectiveDateTime\": \"2016-03-28\",\n" +
                "  \"valueQuantity\": {\n" +
                "    \"value\": 145,\n" +
                "    \"unit\": \"lbs\"\n" +
                "  }\n" +
                "}");

        JSONObject jsData = JSON.parseObject(data);

        log.info("templates: " + templates.toString());
        log.info("jsData: " + jsData.toString());


        // 1. observation，weight
        templates.getJSONObject("subject").put("reference", resource + "/" + id);
        templates.getJSONObject("valueQuantity").put("value", jsData.getJSONObject("weight").get("value"));
        templates.getJSONObject("valueQuantity").put("unit", (String) jsData.getJSONObject("weight").get("unit"));
        templates.put("effectiveDateTime", (String) jsData.get("effectiveDateTime"));


        weightObservation = templates.toString();

        // 2. observation，height
        templates.getJSONObject("code").getJSONArray("coding")
                .getJSONObject(0)
                .put("display", "Body-Height");

        templates.getJSONObject("subject").put("reference", resource + "/" + id);
        templates.getJSONObject("valueQuantity").put("value", jsData.getJSONObject("height").get("value"));
        templates.getJSONObject("valueQuantity").put("unit", (String) jsData.getJSONObject("height").get("unit"));
        templates.put("effectiveDateTime", (String) jsData.get("effectiveDateTime"));

        heightObservation = templates.toString();

        log.info(weightObservation.toString());
        log.info(heightObservation.toString());

        // 3. send fhir request
        String[] rels = new String[2];
        try {
            rels[0] = remoteFhirService.add("Observation", weightObservation);
            rels[1] = remoteFhirService.add("Observation", heightObservation);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }
        return R.success(Arrays.stream(rels).toList().toString());
    }

    @DeleteMapping("/observation/{resource}/{id}")
    public R<String> deleteObservation(@PathVariable String resource, @PathVariable String id) {
        return R.success("test");
    }

    /**
     *  Util
     */
    private String getFhirResource(String uri){
        String[] path = uri.split("/");
        int index = IntStream.range(0, path.length)
                .filter(i -> path[i].equals("fhir"))
                .findFirst()
                .orElse(-1) - 1;
        return path[index];
    }

}


