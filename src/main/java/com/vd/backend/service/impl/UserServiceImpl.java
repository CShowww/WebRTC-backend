package com.vd.backend.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vd.backend.entity.bo.User;
import com.vd.backend.service.UserService;
import com.vd.backend.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    @Override
    public void saveToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null) {
            throw new RuntimeException("无token参数，请重新登录");
        }
        String userId = httpServletRequest.getParameter("userId");
        Date expiresAt;
        String signature;
        try {
            expiresAt = JWT.decode(token).getExpiresAt();
            signature = JWT.decode(token).getSignature();
        } catch (JWTDecodeException j) {
            throw new RuntimeException("401");
        }
        User user = new User();
        user.setToken(signature);
        user.setId(userId);
        user.setExpiredTime(expiresAt);
        this.save(user);
    }
}




