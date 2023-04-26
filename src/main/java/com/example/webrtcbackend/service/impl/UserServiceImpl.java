package com.example.webrtcbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.webrtcbackend.entity.bo.User;
import com.example.webrtcbackend.service.UserService;
import com.example.webrtcbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public User findUserById(String userId) {
        return this.getById(userId);
    }


}




