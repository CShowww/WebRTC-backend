package com.vd.backend.controller;


import com.vd.backend.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/Patient")
@CrossOrigin
public class PatientController {

    @Autowired
    PatientService patientService;

}
