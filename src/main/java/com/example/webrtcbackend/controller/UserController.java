package com.example.webrtcbackend.controller;


import com.example.webrtcbackend.common.R;
import com.example.webrtcbackend.service.FhirService;
import com.example.webrtcbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    FhirService fhirService;

    // 用户服务
//    @Autowired
//    UserService userService;


    @PostMapping("/login")
    public R<String> login(@RequestBody Map map) {
        log.info(map.toString());

        // TODO: User 数据库操作
        // TODO: Fhir Server 交互

        return R.success(map.toString());
    }

}
