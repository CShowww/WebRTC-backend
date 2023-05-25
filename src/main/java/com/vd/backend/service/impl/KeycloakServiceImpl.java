package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.vo.UserInfo;
import com.vd.backend.service.KeycloakService;
import com.vd.backend.util.Utils;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class KeycloakServiceImpl implements KeycloakService {
    @Override
    public String getAccessToken() {
        String authorizeUrl = "https://keycloak-uom2.comp90082-2023.vsbox.dev/auth/realms/master/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "uom2-admin");
        params.add("password", "vojumosibaz#37");
        params.add("grant_type", "password");
        params.add("client_id", "Blue-ring-h1");


        String rel = Utils.sendPostRequest(authorizeUrl, params);
        String token = JSONObject.parseObject(rel).getString("access_token");

        return token;
    }

    @Override
    public int addUser(UserInfo userInfo, String token) {
        String authorizeUrl = "https://keycloak-uom2.comp90082-2023.vsbox.dev/auth/admin/realms/master/users";

        String rel = Utils.sendPostRequest(authorizeUrl, userInfo, token);

        return 1;
    }

    @Override
    public int update(UserInfo userInfo, String token, String id) {
        String authorizeUrl = "https://keycloak-uom2.comp90082-2023.vsbox.dev/auth/admin/realms/master/users/" + id;

        String rel = Utils.sendPutRequest(authorizeUrl, userInfo, token);

        return 1;
    }
}
