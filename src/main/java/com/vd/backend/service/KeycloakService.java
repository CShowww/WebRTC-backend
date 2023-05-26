package com.vd.backend.service;

import com.vd.backend.entity.vo.UserInfo;

public interface KeycloakService {

    String getAccessToken();

    int addUser(UserInfo userInfo, String token);

    int update(UserInfo userInfo, String token, String id);
}
