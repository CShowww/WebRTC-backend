package com.vd.backend.entity.bo;

import lombok.Data;

@Data
public class Connector {

    String patientId;

    String practitionerId;

    String patientName;

    String practitionerName;

    String patientEmail;

    String practitionerEmail;

    // "Update","Delete","Add"
    String type;

}
