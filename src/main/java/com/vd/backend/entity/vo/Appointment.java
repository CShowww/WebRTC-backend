package com.vd.backend.entity.vo;

import lombok.Data;


@Data
public class Appointment {
    private String practitionerId;
    private String practitionerName;
    private String patientId;
    private String patientName;
    private String startTime;
    private String endTime;
    private String status;
    private String description;
    private String note;
    private String patientEmail;
    private String practitionerEmail;
}
