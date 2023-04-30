package com.example.webrtcbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.webrtcbackend.entity.bo.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 63013
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-04-23 19:46:36
* @Entity com.example.webrtcbackend.entity.bo.User.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




