package com.vd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vd.backend.entity.bo.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 63013
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-05-21 22:27:42
* @Entity com.vd.backend.entity.bo.User.User
*/

@Mapper
public interface UserMapper extends BaseMapper<User> {

}




