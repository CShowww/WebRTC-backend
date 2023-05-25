package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.vo.Appointment;
import com.vd.backend.service.AppointmentService;
import com.vd.backend.service.HttpFhirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    HttpFhirService fhirService;

    @Override
    public String Json2String(JSONObject jsonObject) {
        JSONObject outputJson = new JSONObject();
        String id = jsonObject.getString("id");
        String status = jsonObject.getString("status");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = LocalDateTime.parse(jsonObject.getString("start"), inputFormatter).format(outputFormatter);
        String end = LocalDateTime.parse(jsonObject.getString("end"), inputFormatter).format(outputFormatter);
        String description = jsonObject.getString("description");
        String patientId= "", patientName= "", practitionerId = "", practitionerName = "";
        JSONArray participant = jsonObject.getJSONArray("participant");
        for (int i = 0; i < participant.size(); i++) {

            JSONObject res = participant.getJSONObject(i).getJSONObject("actor");
            if(res.getString("reference").substring(0, 7).equals("Patient")){
                patientId = res.getString("reference").split("/")[1];
                patientName = res.getString("display");
            }else if(res.getString("reference").substring(0, 7).equals("Practit")){
                practitionerId = res.getString("reference").split("/")[1];
                practitionerName = res.getString("display");
            }
        }

        outputJson.put("id", id);
        outputJson.put("startTime", start);
        outputJson.put("endTime", end);
        outputJson.put("status", status);
        outputJson.put("practitionerId", practitionerId);
        outputJson.put("practitionerName", practitionerName);
        outputJson.put("patientId", patientId);
        outputJson.put("patientName", patientName);
        outputJson.put("description", description);

        return outputJson.toString();
    }

    @Override
    public JSONObject String2Json(Appointment appointment) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resourceType", "Appointment");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        jsonObject.put("start", LocalDateTime.parse(appointment.getStartTime(), inputFormatter).atOffset(ZoneOffset.UTC).format(outputFormatter));
        jsonObject.put("end", LocalDateTime.parse(appointment.getEndTime(), inputFormatter).atOffset(ZoneOffset.UTC).format(outputFormatter));
        jsonObject.put("status", appointment.getStatus());
        if(appointment.getDescription()!=null){
            jsonObject.put("description", appointment.getDescription());
        }else{
            jsonObject.put("description", "No description");
        }

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

        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    @Override
    public int getUserInfoById(Appointment appointment) {
        String patientName = "", practitionerName = "";
        try {
            String patient = fhirService.get("Patient", appointment.getPatientId());
            JSONObject name = JSON.parseObject(patient).getJSONArray("name").getJSONObject(0);
            String givenName = name.getJSONArray("given").getString(0);
            String familyName = name.getString("family");
            patientName = givenName + " " + familyName;
            appointment.setPatientName(patientName);

            JSONArray telecom = JSON.parseObject(patient).getJSONArray("telecom");
            for(int i = 0; i<telecom.size(); i++){
                if(telecom.getJSONObject(i).getString("system")!=null &&
                        telecom.getJSONObject(i).getString("system").equals("email")){
                    appointment.setPatientEmail(telecom.getJSONObject(i).getString("value"));
                }
            }
            if (appointment.getPatientEmail()==null) return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        try {
            String practitioner = fhirService.get("Practitioner", appointment.getPractitionerId());
            JSONObject name = JSON.parseObject(practitioner).getJSONArray("name").getJSONObject(0);
            String givenName = name.getJSONArray("given").getString(0);
            String familyName = name.getString("family");
            practitionerName = givenName + " " + familyName;
            appointment.setPractitionerName(practitionerName);

            JSONArray telecom = JSON.parseObject(practitioner).getJSONArray("telecom");
            for(int i = 0; i<telecom.size(); i++){
                if(telecom.getJSONObject(i).getString("system")!=null &&
                        telecom.getJSONObject(i).getString("system").equals("email")){
                    appointment.setPractitionerEmail(telecom.getJSONObject(i).getString("value"));
                }
            }
            if (appointment.getPractitionerEmail()==null) return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

}