package com.vd.backend.service;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.vo.Appointment;

public interface AppointmentService {

    public String Json2String(JSONObject jsonObject);

    public JSONObject String2Json(Appointment appointment);
}
