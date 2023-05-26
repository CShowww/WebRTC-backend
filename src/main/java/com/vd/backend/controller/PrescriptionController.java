package com.vd.backend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.HttpFhirService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

        for (int i = 0; entry != null && i < entry.size(); i++) {
            JSONObject jsonObject = new JSONObject();

            JSONObject res = entry.getJSONObject(i).getJSONObject("resource");

            String status = res.getString("status"); //1
            String quantity = res.getJSONObject("quantity").getString("value"); //1
            String recorded = res.getString("whenPrepared"); // 1
            String id = res.getString("id");


            String practitionerId = res.getJSONArray("performer").getJSONObject(0).getJSONObject("actor").getString("reference"); //1
            practitionerId = practitionerId.substring(13);
            String practitionerName = res.getJSONArray("performer").getJSONObject(0).getJSONObject("actor").getString("display"); //1


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

            jsonObject.put("id",id);
            ans.add(jsonObject);
        }

        return R.success(ans.toString());
    }

    @GetMapping("/Patient/{id}")
    public R<String> getPrescriptionById(@PathVariable String id) {
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

        for (int i = 0; entry != null && i < entry.size(); i++) {
            JSONObject jsonObject = new JSONObject();

            JSONObject res = entry.getJSONObject(i).getJSONObject("resource");

            String mId = res.getString("id");
            String status = res.getString("status"); //1
            String quantity = res.getJSONObject("quantity").getString("value"); //1
            String recorded = res.getString("whenPrepared"); // 1
            String patientId = res.getJSONObject("subject").getString("reference"); //1
            patientId = patientId.substring(8);
            if(patientId.equals(id)){
                String patientName = res.getJSONObject("subject").getString("display"); //1

                String practitionerId = res.getJSONArray("performer").getJSONObject(0).getJSONObject("actor").getString("reference"); //1
                practitionerId = practitionerId.substring(13);
                String practitionerName = res.getJSONArray("performer").getJSONObject(0).getJSONObject("actor").getString("display"); //1


                String medicationId = res.getJSONObject("medicationReference").getString("reference"); //1
                String medicationName = res.getJSONObject("medicationReference").getString("display"); //1



                jsonObject.put("practitionerId", practitionerId);
                jsonObject.put("practitionerName", practitionerName);

                jsonObject.put("patientId", patientId);
                jsonObject.put("patientName", patientName);

                jsonObject.put("medicationId", medicationId);
                jsonObject.put("medicationName", medicationName);

                jsonObject.put("quantity", quantity);
                jsonObject.put("status", status);
                jsonObject.put("recorded", recorded);
                jsonObject.put("id",mId);
                ans.add(jsonObject);
            }

        }

        return R.success(ans.toString());
    }

    @PostMapping()
    public R<String> addPrescription(@RequestBody String data) {
        log.info("Post {}", resource);
        String rel = "";

        try {
            rel = fhirService.add(resource, data);
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

    @PutMapping("/{id}")
    public R<String> updatePrescription(@PathVariable String id, @RequestBody String data) {
        log.info("Update {}/{}", resource, id);
        String rel = "";
        try {
            rel = fhirService.update(resource, id, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }

        return R.success(rel);
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

    @GetMapping("/Medication")
    public R<String> getAllMedication() {
        log.info("Get all Medication");
        String rel = "";
        try {
            rel = fhirService.getAll("Medication");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();

        for (int i = 0; entry != null && i < entry.size(); i++) {
            JSONObject jsonObject = new JSONObject();

            JSONObject res = entry.getJSONObject(i).getJSONObject("resource");

            String id = res.getString("id"); //1

            JSONObject JSName = (JSONObject) res.getJSONObject("code").getJSONArray("coding").get(0);
            String name = JSName.getString("display");
            jsonObject.put("id", id);
            jsonObject.put("name", name);

            ans.add(jsonObject);
        }

        return R.success(ans.toString());
    }
}
