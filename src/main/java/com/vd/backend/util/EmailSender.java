package com.vd.backend.util;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.bo.Connector;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    Connector connector;

    public void sendEmail(String recipient, String subject, String content) {
        // 配置发送邮件的属性
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // 发送方邮箱的 SMTP 服务器地址
        props.put("mail.smtp.port", "587"); // 发送方邮箱的 SMTP 服务器端口号

        // 创建认证对象
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("${email.username}", "${email.password}");
            }
        };

        // 创建会话
        Session session = Session.getInstance(props, auth);

        try {
            // 创建邮件消息
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("${email.username}"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(content);

            // 发送邮件
            Transport.send(message);

            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            System.out.println("邮件发送失败：" + e.getMessage());
        }
    }

    public void acceptData(JSONObject data){
        String type = data.getString("type");
        connector.setType(type);

        if(type.equals("Update")){
            connector.setPatientId(data.getString("patientId"));
            connector.setPatientName(data.getString("patientName"));
        }
        else if(type.equals("Delete")){
            connector.setPractitionerId(data.getString("practitionerId"));
            connector.setPractitionerName(data.getString("practitionerName"));
        }
        else{
            connector.setPatientId(data.getString("patientId"));
            connector.setPatientName(data.getString("patientName"));
            connector.setPractitionerId(data.getString("practitionerId"));
            connector.setPractitionerName(data.getString("practitionerName"));
        }
    }

}
