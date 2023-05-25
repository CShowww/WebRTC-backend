package com.vd.backend.util;

import com.vd.backend.entity.vo.UserInfo;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Utils {

    public static String sendPostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
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
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<UserInfo> requestEntity = new HttpEntity<UserInfo>(userInfo, headers);
        System.out.println(requestEntity.toString());
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
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
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<UserInfo> requestEntity = new HttpEntity<UserInfo>(userInfo, headers);
        System.out.println(requestEntity.toString());
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if(!response.hasBody()){
            return null;
        }


        return response.getBody();
    }

}
