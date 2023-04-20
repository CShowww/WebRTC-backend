package com.example.webrtcbackend.service;


import com.example.webrtcbackend.entity.Patient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value="fhir-server",
        url="http://localhost:8084/fhir"
)
@Primary
public interface FhirService {
    // CRUD Operation
    @RequestMapping(value = "/Patient/{id}", method = RequestMethod.GET)
    public String getPatientById(@PathVariable String id);

    @RequestMapping(value = "/Patient", method = RequestMethod.POST)
    public String createPatient(Patient patient);

    // TODO: HANDLE ALL MAPPING
}
