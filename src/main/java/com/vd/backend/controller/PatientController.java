package com.vd.backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/Patient")
@CrossOrigin
public class PatientController {

    @Autowired
    private PatientService patientService;


}
