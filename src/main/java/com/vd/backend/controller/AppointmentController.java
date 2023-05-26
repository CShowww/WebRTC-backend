package com.vd.backend.controller;

import ca.uhn.fhir.rest.annotation.Transaction;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.common.ResultCode;
import com.vd.backend.entity.bo.Connector;
import com.vd.backend.entity.vo.Appointment;
import com.vd.backend.service.AppointmentService;

import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.util.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/appointment")
@CrossOrigin
public class AppointmentController {

    @Autowired
    private AsynFhirService fhirService;

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
        List<String> rel = new ArrayList<>();
        try {
            rel = fhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        JSONArray ans = new JSONArray();

        for (String s: rel) {
            JSONObject object = JSON.parseObject(s);
            ans.add(JSONObject.parseObject(appointmentService.Json2String(object)));
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

        Thread t = new Thread(new sendEmailTask(appointment, "Update"));
        t.start();


        return R.success(rel);
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable String id) {
        log.info("Delete appointment {}", id);
        String rel = "";
        try{
            rel = fhirService.get(resource, id);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(ResultCode.USER_NOT_EXIST_ERROR);
        }
        JSONObject appointmentJson = JSONObject.parseObject(appointmentService.Json2String(JSONObject.parseObject(rel)));
        Appointment appointment = new Appointment();
        appointment.setPatientId(appointmentJson.getString("patientId"));
        appointment.setPractitionerId(appointmentJson.getString("practitionerId"));
        if(appointmentService.getUserInfoById(appointment) < 0){
            return R.error("Get user resource false.");
        }

        try {
            rel = fhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("DELETE fail");
        }

        Thread t = new Thread(new sendEmailTask(appointment, "Delete"));
        t.start();

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


        Thread t = new Thread(new sendEmailTask(appointment, "Add"));
        t.start();


        return r;
    }

    class sendEmailTask implements Runnable {

        String type = null;

        Appointment appointment = null;
        public sendEmailTask(Appointment appointment, String type) {
            this.type = type;
            this.appointment = appointment;
        }

        @Override
        public void run() {
            // send email
            Connector connector = new Connector();
            BeanUtils.copyProperties(appointment, connector);
            connector.setType(type);
            EmailSender emailSender = new EmailSender();
            try{
                emailSender.sendEmail(connector);
            }catch (MessagingException e){
                log.info("Send email fails!");
            }
        }
    }

}
