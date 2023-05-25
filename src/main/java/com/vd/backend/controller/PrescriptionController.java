package com.vd.backend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.HttpFhirService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/MedicationDispense")
@CrossOrigin
public class PrescriptionController {

    @Autowired
    HttpFhirService fhirService;

    String resource = "MedicationDispense";


    @GetMapping("/{id}")
    public R<String> getPrescription(@PathVariable String id) {

        log.info("Get {}/{}", resource, id);

        String rel = "";
        try{
            rel = fhirService.get(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }

        return R.success(rel);
    }


    @GetMapping()
    public R<String> getAllPrescription() {
        log.info("Get all Prescription {}", resource);
        String rel = "";
        try {
            rel = fhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();

        for (int i = 0; entry.size() != 0 && i < entry.size(); i++) {
            JSONObject jsonObject = new JSONObject();

            JSONObject res = entry.getJSONObject(i).getJSONObject("resource");

            String status = res.getString("status"); //1
            String quantity = res.getJSONObject("quantity").getString("value"); //1
            String recorded = res.getString("whenPrepared"); // 1


            String practitionerId = res.getJSONObject("performer").getJSONObject("actor").getString("reference"); //1
            practitionerId = practitionerId.substring(13);
            String practitionerName = res.getJSONObject("performer").getJSONObject("actor").getString("display"); //1


            String medicationId = res.getJSONObject("medicationReference").getString("reference"); //1
            String medicationName = res.getJSONObject("medicationReference").getString("display"); //1

            String patientId = res.getJSONObject("subject").getString("reference"); //1
            patientId = patientId.substring(8);
            String patientName = res.getJSONObject("subject").getString("display"); //1

            jsonObject.put("practitionerId", practitionerId);
            jsonObject.put("practitionerName", practitionerName);

            jsonObject.put("patientId", patientId);
            jsonObject.put("patientName", patientName);

            jsonObject.put("medicationId", medicationId);
            jsonObject.put("medicationName", medicationName);

            jsonObject.put("quantity", quantity);
            jsonObject.put("status", status);
            jsonObject.put("recorded", recorded);

            ans.add(jsonObject);
        }

        return R.success(ans.toString());
    }

    @PostMapping()
    public R<String> addPrescription(@RequestBody String data) {
        log.info("Post {}", resource);
        String rel = "";

        JSONObject templates = JSON.parseObject("{\n" +
                "    \"resourceType\": \"MedicationDispense\",\n" +
                "    \"id\": \"1573\",\n" +
                "    \"meta\": {\n" +
                "        \"versionId\": \"1\",\n" +
                "        \"lastUpdated\": \"2023-05-25T23:02:05.041+10:00\"\n" +
                "    },\n" +
                "    \"status\": \"in-progress\",\n" +
                "    \"medicationReference\": {\n" +
                "        \"reference\": \"Medication/1538\",\n" +
                "        \"display\": \"Vancomycin Hydrochloride (VANCOMYCIN HYDROCHLORIDE)\"\n" +
                "    },\n" +
                "    \"subject\": {\n" +
                "        \"reference\": \"Patient/1535\",\n" +
                "        \"display\": \"VD-Patient-Test\"\n" +
                "    },\n" +
                "    \"performer\": [\n" +
                "        {\n" +
                "            \"actor\": {\n" +
                "                \"reference\": \"Practitioner/1522\",\n" +
                "                \"display\": \"Jenny\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"quantity\": {\n" +
                "        \"value\": 3,\n" +
                "        \"system\": \"http://snomed.info/sct\",\n" +
                "        \"code\": \"415818006\"\n" +
                "    },\n" +
                "    \"whenPrepared\": \"2023-05-25T10:20:00Z\"\n" +
                "}");


        JSONObject jsData = JSON.parseObject(data);

        templates.getJSONObject("subject").put("reference", "Patient/" + jsData.get("patientId"));
        templates.getJSONObject("subject").put("display", jsData.get("patientName"));

        templates.getJSONObject("performer").getJSONObject("actor").put("reference", "Practitioner/" + jsData.get("practitionerId"));
        templates.getJSONObject("performer").getJSONObject("actor").put("display", jsData.get("practitionerName"));

        templates.getJSONObject("medicationReference").put("reference", "Medication/" + jsData.get("medicationId"));
        templates.getJSONObject("medicationReference").put("display", jsData.get("medicationName"));

        templates.put("status", jsData.get("status"));
        templates.put("whenPrepared", (new Date().toString()));
        templates.getJSONObject("quantity").put("value",jsData.get("quantity"));

        try {
            rel = fhirService.add(resource, templates.toString());
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }

        R<String> r = R.success(rel);
        return r;
    }

    @PutMapping("/{id}")
    public R<String> updatePrescription(@PathVariable String id, @RequestBody String data) {
        log.info("Update {}/{}", resource, id);
        String rel = "";

        JSONObject templates = JSON.parseObject("{\n" +
                "    \"resourceType\": \"MedicationDispense\",\n" +
                "    \"id\": \"1573\",\n" +
                "    \"meta\": {\n" +
                "        \"versionId\": \"1\",\n" +
                "        \"lastUpdated\": \"2023-05-25T23:02:05.041+10:00\"\n" +
                "    },\n" +
                "    \"status\": \"in-progress\",\n" +
                "    \"medicationReference\": {\n" +
                "        \"reference\": \"Medication/1538\",\n" +
                "        \"display\": \"Vancomycin Hydrochloride (VANCOMYCIN HYDROCHLORIDE)\"\n" +
                "    },\n" +
                "    \"subject\": {\n" +
                "        \"reference\": \"Patient/1535\",\n" +
                "        \"display\": \"VD-Patient-Test\"\n" +
                "    },\n" +
                "    \"performer\": [\n" +
                "        {\n" +
                "            \"actor\": {\n" +
                "                \"reference\": \"Practitioner/1522\",\n" +
                "                \"display\": \"Jenny\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"quantity\": {\n" +
                "        \"value\": 3,\n" +
                "        \"system\": \"http://snomed.info/sct\",\n" +
                "        \"code\": \"415818006\"\n" +
                "    },\n" +
                "    \"whenPrepared\": \"2023-05-25T10:20:00Z\"\n" +
                "}");


        JSONObject jsData = JSON.parseObject(data);

        templates.getJSONObject("subject").put("reference", "Patient/" + jsData.get("patientId"));
        templates.getJSONObject("subject").put("display", jsData.get("patientName"));

        templates.getJSONObject("performer").getJSONObject("actor").put("reference", "Practitioner/" +jsData.get("practitionerId"));
        templates.getJSONObject("performer").getJSONObject("actor").put("display", jsData.get( "practitionerName"));

        templates.getJSONObject("medicationReference").put("reference", "Medication/" + jsData.get("medicationId"));
        templates.getJSONObject("medicationReference").put("display", jsData.get("medicationName"));

        templates.put("status", jsData.get("status"));
        templates.put("whenPrepared", (new Date()).toString());
        templates.getJSONObject("quantity").put("value",jsData.get("quantity"));

        try {
            rel = fhirService.update(resource, id, templates.toString());
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


    @DeleteMapping("/{id}")
    public R<String> deletePrescription(@PathVariable String id) {
        log.info("Delete {}/{}", resource, id);
        String rel = "";
        try {
            rel = fhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("DELETE fail");
        }

        return R.success(rel);
    }
}
