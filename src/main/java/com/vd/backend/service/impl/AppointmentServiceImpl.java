package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.vo.Appointment;
import com.vd.backend.service.AppointmentService;
import org.springframework.stereotype.Service;

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
        jsonObject.put("start", appointment.getStartTime());
        jsonObject.put("end", appointment.getEndTime());
        jsonObject.put("status", appointment.getStatus());

        JSONArray participant = new JSONArray();

        JSONObject practitionerJson = new JSONObject();
        JSONObject practitionerType = JSON.parseObject("\"type\":\n" +
                "            [{\n" +
                "\t\"coding\": [{\n" +
                "\t\t\"system\": \"http://www.hl7.org/sitemap.cfm\",\n" +
                "\t\t\"code\": \"ATND\"\n" +
                "\t}]");
        JSONObject practitionerActor = new JSONObject();
        practitionerActor.put("reference", "Practitioner/" + appointment.getPractitionerId());
        practitionerActor.put("display", appointment.getPractitionerName());
        practitionerJson.put("type", practitionerType);
        practitionerJson.put("actor", practitionerActor);
        practitionerJson.put("required", "required");
        practitionerJson.put("status", "accepted");

        JSONObject patientJson = new JSONObject();
        JSONObject patientActor = new JSONObject();
        patientActor.put("reference", "Practitioner/" + appointment.getPatientId());
        patientActor.put("display", appointment.getPatientName());
        patientJson.put("actor", practitionerActor);
        patientJson.put("required", "required");
        patientJson.put("status", "accepted");

        participant.add(practitionerJson);
        participant.add(patientJson);

        jsonObject.put("participant", participant);

        return jsonObject;
    }
}
