package com.example.webrtcbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.webrtcbackend.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
}
