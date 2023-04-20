package com.example.webrtcbackend.controller;


import com.example.webrtcbackend.common.R;
import com.example.webrtcbackend.service.FhirService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/Patient")
public class PatientController {

    @Autowired
    private  FhirService service;

    @GetMapping("/{id}")
    public R<String> getById(@PathVariable String id) {
        log.info("Search Patient via remote Fhir Server " + id);

        String rel = service.getPatientById(id);
        if (rel.equals("FAIL")) {
            return R.error("call: " + this.getClass().getName() + " fail");
        }

        log.info(rel);

        return R.success(rel);
    }

    @GetMapping("/hello")
    public R<String> sayHi() {
        return R.success("hi");
    }
}

