package com.vd.backend.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class Appointment {

    private String practitionerId;
    private String practitionerName;
    private String patientId;
    private String patientName;
    private Date startTime;
    private Date endTime;
    private String status;
}
