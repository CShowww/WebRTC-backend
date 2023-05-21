package com.vd.backend.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class Appointment {

    private String practitionerId;
    private String practitionerName;
    private String patientId;
    private String patientName;
    private String startTime;
    private String endTime;
    private String status;
}
