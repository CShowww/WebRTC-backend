package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vd.backend.entity.bo.User;
import com.vd.backend.service.FhirService;
import com.vd.backend.service.UserService;
import com.vd.backend.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author 63013
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-04-23 19:46:36
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FhirService fhirService;

    @Override
    public User findUserById(String userId) {
        return this.getById(userId);
    }

    @Override
    public String saveToken(HttpServletRequest httpServletRequest, String data) {
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null) {
            throw new RuntimeException("无token参数，请重新登录");
        }

        Date expiresAt;
        String signature;
        try {
            expiresAt = JWT.decode(token).getExpiresAt();
            signature = JWT.decode(token).getSignature();
        } catch (JWTDecodeException j) {
            throw new RuntimeException("401");
        }

        JSONObject jsonObject = JSONObject.parseObject(data);
        String id = jsonObject.getJSONObject("profile").getString("sub");

        User user = this.getById(id);

        if(user==null){
            user = new User();
            user.setToken(signature);
            user.setExpiredTime(expiresAt);
            user.setId(id);
            System.out.println(jsonObject.toString());
            JSONArray roles = jsonObject.getJSONObject("profile").getJSONObject("resource_access").getJSONObject("virtual-doctor").getJSONArray("roles");

            for(int i = 0; i< roles.size(); i++){
                String actor = roles.getString(i);
                if(actor.equals("virtual-doctor-practitioner")){
                    String rel = "";
                    String template = "{\n" +
                            "    \"resourceType\": \"Practitioner\"\n" +
                            "}";
                    try {
                        rel = fhirService.add("Practitioner", template);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    user.setRole(0);
                    user.setFhirId(JSONObject.parseObject(rel).getString("id"));
                    this.save(user);
                    return user.getFhirId();
                }else if(actor.equals("virtual-doctor-patient")){
                    String rel = "";
                    String template = "{\n" +
                            "    \"resourceType\": \"Patient\"\n" +
                            "}";
                    try {
                        rel = fhirService.add("Patient", template);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    user.setRole(1);
                    user.setFhirId(JSONObject.parseObject(rel).getString("id"));
                    this.save(user);
                    return user.getFhirId();
                }
            }
            return null;
        }

        return user.getFhirId();


    }

}




