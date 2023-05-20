package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.vo.Appointment;
import com.vd.backend.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Override
    public String Json2String(JSONObject jsonObject) {
        return null;
    }

    @Override
    public JSONObject String2Json(Appointment appointment) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resourceType", "Appointment");
        try{
            DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            jsonObject.put("start", format.parse(appointment.getStartTime()));
            jsonObject.put("end", format.parse(appointment.getEndTime()));
        }catch (ParseException e){
            System.out.println(e.getMessage());
        }
        jsonObject.put("status", appointment.getStatus());

        JSONArray participant = new JSONArray();

        JSONObject practitionerJson = new JSONObject();
        JSONArray practitionerType = new JSONArray();
        practitionerType.add(JSON.parseObject("""
                {
                \t"coding": [{
                \t\t"system": "http://www.hl7.org/sitemap.cfm",
                \t\t"code": "ATND"
                \t}]
                }]
                }"""));
        JSONObject practitionerActor = new JSONObject();
        practitionerActor.put("reference", "Practitioner/" + appointment.getPractitionerId());
        practitionerActor.put("display", appointment.getPractitionerName());
        practitionerJson.put("type", practitionerType);
        practitionerJson.put("actor", practitionerActor);
        practitionerJson.put("required", "required");
        practitionerJson.put("status", "accepted");

        JSONObject patientJson = new JSONObject();
        JSONObject patientActor = new JSONObject();
        patientActor.put("reference", "Patient/" + appointment.getPatientId());
        patientActor.put("display", appointment.getPatientName());
        patientJson.put("actor", patientActor);
        patientJson.put("required", "required");
        patientJson.put("status", "accepted");

        participant.add(practitionerJson);
        participant.add(patientJson);

        jsonObject.put("participant", participant);

        System.out.println(jsonObject);
        return jsonObject;
    }
}
