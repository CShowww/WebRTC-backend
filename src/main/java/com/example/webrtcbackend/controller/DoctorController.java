package com.example.webrtcbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.webrtcbackend.common.R;
import com.example.webrtcbackend.entity.Doctor;
import com.example.webrtcbackend.service.DoctorService;
import com.example.webrtcbackend.service.Impl.DoctorServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/doctor")
public class DoctorController {
    public DoctorServiceImpl doctorService;

    @PostMapping("/login")
    public R<Doctor> login(HttpServletRequest request, @RequestBody Doctor doctor){
        // md5
        String password = doctor.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Doctor::getUsername, doctor.getUsername());
        Doctor doc = doctorService.getOne(queryWrapper);

        if (doc == null) {
            return R.error("Login failed.");
        }

        if (!doc.getPassword().equals(password)) {
            return R.error("Password is wrong.");
        }

        if (doc.getStatus() == 0) {
            return R.error("This account is prohibition of use.");
        }

        request.getSession().setAttribute("doctor", doc.getId());
        return R.success(doc);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        
        request.getSession().removeAttribute("doctor");
        return R.success("Log out success.");
    }
}
