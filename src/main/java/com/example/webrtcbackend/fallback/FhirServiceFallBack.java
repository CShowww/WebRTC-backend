package com.example.webrtcbackend.fallback;

import com.example.webrtcbackend.common.Status;
import com.example.webrtcbackend.entity.Patient;
import com.example.webrtcbackend.service.FhirService;
import org.springframework.stereotype.Component;

@Component
public class FhirServiceFallBack implements FhirService {
    @Override
    public String getPatientById(String id) {
        return "FAIL";
    }

    @Override
    public String createPatient(Patient patient) {
        return "FAIL";
    }
}
