package com.vd.backend.util;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UtilTest {
    private EmailSender emailSender = new EmailSender();
    @Test
    public void testPatientSend() throws MessagingException {
        JSONObject data = new JSONObject();
        data.put("patientId","1498");
        data.put("patientName","CangJian Gao");
        data.put("patientEmail","CShowwww@gmail.com");
        data.put("type","Delete");
        emailSender.sendEmail(data);
    }
}
