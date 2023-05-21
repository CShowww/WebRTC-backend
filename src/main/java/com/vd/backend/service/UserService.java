package com.vd.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vd.backend.entity.bo.User;
import jakarta.servlet.http.HttpServletRequest;


/**
* @author 63013
* @description 针对表【user】的数据库操作Service
* @createDate 2023-04-23 19:46:36
*/
public interface UserService extends IService<User> {

    public User findUserById(String userId);

    public void saveToken(HttpServletRequest httpServletRequest);
}
