package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vd.backend.entity.bo.User;
import com.vd.backend.service.AsyncFhirService;
import com.vd.backend.service.KeycloakService;
import com.vd.backend.service.UserService;
import com.vd.backend.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 63013
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-04-23 19:46:36
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AsyncFhirService fhirService;

    @Autowired
    private KeycloakService keycloakService;

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

        JSONObject jsonObject = JSONObject.parseObject(data);
        String id = jsonObject.getJSONObject("profile").getString("sub");

        String admin_token = "";
        String rel = "";
        try {
            admin_token = keycloakService.getAccessToken();
        } catch (HttpClientErrorException e) {
            System.out.println("Get token fail");
            return null;
        }

        try {
            rel = keycloakService.getUser(admin_token, id);
        } catch (HttpClientErrorException e) {
            System.out.println("Get user fail");
            return null;
        }

        JSONObject relJson = JSONObject.parseObject(rel);
        if (relJson.getString("error") != null) {
            System.out.println("Get user is null");
            return null;
        }

        JSONObject profile = jsonObject.getJSONObject("profile");
        JSONArray roles = profile.getJSONObject("resource_access").getJSONObject("virtual-doctor").getJSONArray("roles");

        if(relJson.getJSONObject("attributes")==null || relJson.getJSONObject("attributes").getJSONArray("fhirId")==null){
            for (int i = 0; i < roles.size(); i++) {
                String actor = roles.getString(i);
                if (actor.equals("virtual-doctor-practitioner")) {
                    String template = "{\n" +
                            "    \"resourceType\": \"Practitioner\"\n" +
                            "}";
                    JSONObject jsonTemplate = JSONObject.parseObject(template);
                    //parse Name
                    JSONArray nameArray = new JSONArray();
                    JSONObject name = new JSONObject();
                    name.put("use", "official");
                    name.put("family", profile.getString("family_name"));
                    List<String> givenName = Arrays.asList(profile.getString("given_name"));
                    name.put("given", givenName);
                    nameArray.add(name);
                    jsonTemplate.put("name", nameArray);
                    //parse Email
                    JSONArray telecomArray = new JSONArray();
                    JSONObject telecom = new JSONObject();
                    telecom.put("system", "email");
                    telecom.put("value", profile.getString("email"));
                    telecom.put("use", "work");
                    telecomArray.add(telecom);
                    jsonTemplate.put("telecom", telecomArray);
                    //parse gender
                    if (profile.getString("gender") != null) {
                        jsonTemplate.put("gender", profile.getString("gender").toLowerCase());
                    }
                    //parse birth
                    jsonTemplate.put("birthDate", profile.getString("birth_date"));
                    System.out.println(jsonTemplate);

                    String result = "";
                    try {
                        result = fhirService.add("Practitioner", jsonTemplate.toString());
                        log.info("add Practitioner {}", jsonTemplate.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    String fhir_id = JSONObject.parseObject(result).getString("id");
                    List<String> fhir_ids = new ArrayList<>();
                    fhir_ids.add(fhir_id);
                    if(relJson.getJSONObject("attributes") == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("fhirId", fhir_ids);
                        relJson.put("attributes", obj);

                    } else {
                        relJson.getJSONObject("attributes").put("fhirId", fhir_ids);
                    }

                    keycloakService.updateFhirId(admin_token, id, relJson);
                    return fhir_id;
                } else if (actor.equals("virtual-doctor-patient")) {
                    String template = "{\n" +
                            "    \"resourceType\": \"Patient\"\n" +
                            "}";

                    JSONObject jsonTemplate = JSONObject.parseObject(template);
                    //parse Name
                    JSONArray nameArray = new JSONArray();
                    JSONObject name = new JSONObject();
                    name.put("use", "official");
                    name.put("family", profile.getString("family_name"));
                    List<String> givenName = Arrays.asList(profile.getString("given_name"));
                    name.put("given", givenName);
                    nameArray.add(name);
                    jsonTemplate.put("name", nameArray);
                    //parse Email
                    JSONArray telecomArray = new JSONArray();
                    JSONObject telecom = new JSONObject();
                    telecom.put("system", "email");
                    telecom.put("value", profile.getString("email"));
                    telecom.put("use", "work");
                    telecomArray.add(telecom);
                    jsonTemplate.put("telecom", telecomArray);
                    //parse gender
                    if (profile.getString("gender") != null) {
                        jsonTemplate.put("gender", profile.getString("gender").toLowerCase());
                    }
                    //parse birth
                    jsonTemplate.put("birthDate", profile.getString("birth_date"));
                    System.out.println(jsonTemplate);

                    String result = "";
                    try {
                        result = fhirService.add("Patient", jsonTemplate.toString());
                        log.info("add Patient {}", jsonTemplate.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    String fhir_id = JSONObject.parseObject(result).getString("id");
                    List<String> fhir_ids = new ArrayList<>();
                    fhir_ids.add(fhir_id);

                    if(relJson.getJSONObject("attributes") == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("fhirId", fhir_ids);
                        relJson.put("attributes", obj);

                    } else {
                        relJson.getJSONObject("attributes").put("fhirId", fhir_ids);
                    }

                    keycloakService.updateFhirId(admin_token, id, relJson);
                    return fhir_id;
                }
            }
            return null;
        }
        return relJson.getJSONObject("attributes").getJSONArray("fhirId").getString(0);
    }

}




