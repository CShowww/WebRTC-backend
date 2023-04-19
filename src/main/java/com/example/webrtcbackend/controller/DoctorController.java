package com.example.webrtcbackend.controller;

import com.example.webrtcbackend.service.DoctorService;
import com.example.webrtcbackend.service.Impl.DoctorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/doctor")
public class DoctorController {
    public DoctorServiceImpl doctorService;
}
