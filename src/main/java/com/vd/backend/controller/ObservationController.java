package com.vd.backend.controller;


import com.vd.backend.service.ObservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/Observation")
@CrossOrigin
public class ObservationController {

    @Autowired
    ObservationService observationService;
}
