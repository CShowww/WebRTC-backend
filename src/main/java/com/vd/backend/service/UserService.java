package com.vd.backend.service;

import com.vd.backend.entity.bo.User;

import javax.servlet.http.HttpServletRequest;


/**
* @author 63013
* @description 针对表【user】的数据库操作Service
* @createDate 2023-04-23 19:46:36
*/
public interface UserService{

    public User findUserById(String userId);

    public String saveToken(HttpServletRequest httpServletRequest, String data);


}
