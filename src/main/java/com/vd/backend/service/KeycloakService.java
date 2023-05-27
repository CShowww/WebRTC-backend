package com.vd.backend.service;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.vo.UserInfo;

public interface KeycloakService {

    String getAccessToken();

    int addUser(UserInfo userInfo, String token);

    int update(UserInfo userInfo, String token, String id);

    String getUser(String token, String id);

    int updateFhirId(String token, String id, JSONObject jsonObject);
}
