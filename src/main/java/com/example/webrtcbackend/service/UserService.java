package com.example.webrtcbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.webrtcbackend.entity.bo.User;

/**
* @author 63013
* @description 针对表【user】的数据库操作Service
* @createDate 2023-04-23 19:46:36
*/
public interface UserService extends IService<User> {

    User findUserById(String userId);
}
