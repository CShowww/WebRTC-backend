package com.vd.backend.util;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.vo.UserInfo;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Utils {

    public static String sendPostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if(!response.hasBody()){
            return null;
        }

        return response.getBody();
    }

    public static String sendPostRequest(String url, UserInfo userInfo, String token){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpMethod method = HttpMethod.POST;

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserInfo> requestEntity = new HttpEntity<UserInfo>(userInfo, headers);
        System.out.println(requestEntity.toString());

        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if(!response.hasBody()){
            return null;
        }

        return response.getBody();
    }

    public static String sendPutRequest(String url, UserInfo userInfo, String token){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpMethod method = HttpMethod.PUT;

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserInfo> requestEntity = new HttpEntity<UserInfo>(userInfo, headers);
        System.out.println(requestEntity.toString());

        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if(!response.hasBody()){
            return null;
        }


        return response.getBody();
    }

    public static String sendGetRequest(String url, String token) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpMethod method = HttpMethod.GET;

        HttpEntity<UserInfo> requestEntity = new HttpEntity<UserInfo>(headers);
        System.out.println(requestEntity.toString());

        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if(!response.hasBody()){
            return null;
        }


        return response.getBody();
    }

    public static String UpdateFhirIdRequest(String url, JSONObject jsonObject, String token){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpMethod method = HttpMethod.PUT;

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<String>(jsonObject.toString(), headers);
        System.out.println(requestEntity.toString());

        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if(!response.hasBody()){
            return null;
        }


        return response.getBody();
    }
}
