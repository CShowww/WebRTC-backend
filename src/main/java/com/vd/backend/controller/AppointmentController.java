package com.vd.backend.controller;

import ca.uhn.fhir.rest.annotation.Transaction;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.FhirService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/appointment")
@CrossOrigin
public class AppointmentController {

    @Autowired
    private FhirService fhirService;

    String resource = "Appointment";

    @GetMapping("/{id}")
    public R<String> get(@PathVariable String id, HttpServletRequest request) {

        log.info("Get appointment {}", id);

        String rel = "";
        try{
            rel = fhirService.get(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }

        return R.success(rel);
    }

    @GetMapping("/getAll")
    public R<String> getAll() {
        log.info("Get all appointments");
        String rel = "";
        try {
            rel = fhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();
        for (int i = 0; i < entry.size(); i++) {

            JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
            res.remove("meta");
            ans.add(res);
        }

        return R.success(ans.toString());
    }

    @GetMapping("/getById/{id}")
    public R<String> getById(@PathVariable String id) {
        log.info("Get all appointments related to practitioner {}", id);
        String rel = "";
        try {
            rel = fhirService.getById(resource, id);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();

        if(entry!=null){
            for (int i = 0; i < entry.size(); i++) {

                JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
                res.remove("meta");
                ans.add(res);
            }
        }


        return R.success(ans.toString());
    }

    @PutMapping("/{id}")
    public R<String> update(@PathVariable String id, @RequestBody String data) {
        log.info("Update appointment {}", id);
        String rel = "";
        try {
            rel = fhirService.update(resource, id, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("UPDATE fail");
        }
        return R.success(rel);
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable String id) {
        log.info("Delete appointment {}", id);
        String rel = "";
        try {
            rel = fhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("DELETE fail");
        }

        return R.success(rel);
    }

    @PostMapping("/{id}")
    @Transaction
    public R<String> add(@PathVariable String id, @RequestBody String data) {
        log.info("Post {}", resource);

        JSONObject appointmentJson = JSON.parseObject(data);
        Date start = appointmentJson.getDate("start");
        Date end = appointmentJson.getDate("end");

        String rel = "";
        try {
            rel = fhirService.getById(resource, id);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }

        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");
        if(entry!=null) {
//            System.out.println("Entry is not null!");
            for (int i = 0; i < entry.size(); i++) {

                JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
                Date bookStart = res.getDate("start");
                Date bookEnd = res.getDate("end");
                if (start.after(bookStart) && start.before(bookEnd)) {
                    return R.error("Current time slot is not available");
                } else if (end.after(bookStart) && end.before(bookEnd)) {
                    return R.error("Current time slot is not available");
                } else if (start.compareTo(bookStart)<=0 && end.compareTo(bookEnd)>=0) {
                    return R.error("Current time slot is not available");
                }
            }
        }


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
}
