package com.example.webrtcbackend.controller;


import com.example.webrtcbackend.common.R;
import com.example.webrtcbackend.entity.bo.User;
import com.example.webrtcbackend.service.FhirService;
import com.example.webrtcbackend.service.UserService;
import com.example.webrtcbackend.token.UserLoginToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    // 用户服务
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public R<String> login(HttpServletRequest httpServletRequest) {
        log.info("Save Token");
        userService.saveToken(httpServletRequest);

        return R.success();
    }

}
