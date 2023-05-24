package com.vd.backend.controller;

import ca.uhn.fhir.rest.annotation.Transaction;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.entity.vo.Appointment;
import com.vd.backend.service.AppointmentService;
import com.vd.backend.service.FhirService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/appointment")
@CrossOrigin
public class AppointmentController {

    @Autowired
    private FhirService fhirService;

    @Autowired
    private AppointmentService appointmentService;

    String resource = "Appointment";

    /**
     * Get Fhir Appointment via ID
     * @param id
     * @param request
     * @return
     */
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

        return R.success(appointmentService.Json2String(JSONObject.parseObject(rel)));
    }

    /**
     * List all appointment
     * @return
     */
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
            ans.add(JSONObject.parseObject(appointmentService.Json2String(res)));
        }

        return R.success(ans.toString());
    }

    /**
     * Get all appointments related to a practitioner
     * @param id
     * @return
     */
    @GetMapping("/getById/practitioner/{id}")
    public R<String> getByPractitionerId(@PathVariable String id) {
        log.info("Get all appointments related to practitioner {}", id);
        String rel = "";
        try {
            rel = fhirService.getByPractitionerId(resource, id);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();

        if(entry!=null){
            for (int i = 0; i < entry.size(); i++) {

                JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
                ans.add(JSONObject.parseObject(appointmentService.Json2String(res)));
            }
        }

        return R.success(ans.toString());
    }

    /**
     * Get all appointments related to a patient
     * @param id
     * @return
     */
    @GetMapping("/getById/patient/{id}")
    public R<String> getByPatientId(@PathVariable String id) {
        log.info("Get all appointments related to practitioner {}", id);
        String rel = "";
        try {
            rel = fhirService.getByPatientId(resource, id);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();

        if(entry!=null){
            for (int i = 0; i < entry.size(); i++) {

                JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
                ans.add(JSONObject.parseObject(appointmentService.Json2String(res)));
            }
        }

        return R.success(ans.toString());
    }

    @PutMapping("/{id}")
    public R<String> update(@PathVariable String id, @RequestBody Appointment appointment) {
        log.info("Update appointment {}", id);
        String rel = "";
        if(appointmentService.getUserInfoById(appointment) < 0){
            return R.error("Get user resource false.");
        }
        JSONObject data = appointmentService.String2Json(appointment);
        data.put("id", id);
        try {
            rel = fhirService.update(resource, id, data.toString());
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("UPDATE fail");
        }
        // send email

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

    @PostMapping
    @Transaction
    public R<String> add(@RequestBody Appointment appointment) {
        log.info("Post {}", resource);

        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime start = null, end = null;
        start = LocalDateTime.parse(appointment.getStartTime(), formatter);
        end = LocalDateTime.parse(appointment.getEndTime(), formatter);

        String rel = "";
        try {
            rel = fhirService.getByPractitionerId(resource, appointment.getPractitionerId());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }

        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");
        if(entry!=null) {
//            System.out.println("Entry is not null!");
            for (int i = 0; i < entry.size(); i++) {

                JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
                pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDateTime bookStart = LocalDateTime.parse(res.getString("start"), formatter);
                LocalDateTime bookEnd = LocalDateTime.parse(res.getString("start"), formatter);

                if (start.isAfter(bookStart) && start.isBefore(bookEnd)) {
                    return R.error("Current time slot is not available");
                } else if (end.isAfter(bookStart) && end.isBefore(bookEnd)) {
                    return R.error("Current time slot is not available");
                } else if (start.compareTo(bookStart) <= 0 && end.compareTo(bookEnd) >= 0) {
                    return R.error("Current time slot is not available");
                }
            }
        }

        if(appointmentService.getUserInfoById(appointment) < 0){
            return R.error("Get user resource false.");
        }
        JSONObject data = appointmentService.String2Json(appointment);

        try {
            rel = fhirService.add(resource, data.toString());
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
