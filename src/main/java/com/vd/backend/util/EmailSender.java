package com.vd.backend.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.bo.Connector;
import com.vd.backend.service.FhirService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
public class EmailSender {
    private Connector connector;
    private Session session;
    private Message message;
    private String content;
    private String[] recipient;

    @Autowired
    private FhirService fhirService;
    // subject  = "主题";
    // content = "内容。"
    // recipient = "recipient@example.com"; // 接收方邮箱地址

    public void init(){
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
        session = Session.getInstance(props, auth);
        message = new MimeMessage(session);
        recipient = new String[2];// practitioner and patient

    }

    public void sendEmail(JSONObject data) {
        parseData(data);
        log.info("Data from front end : {}",data);
        init();

        if(connector.getType().equals("Add")){
            content = "One patient made an appointment!";
            send2Patient(connector.getPatientId(),"Patient", content);
            send2Practitioner(connector.getPractitionerId(), "Practitioner", content);
        }
        else if(connector.getType().equals("Update")){
            content = "The patient updated the appointment.";
            send2Practitioner(connector.getPractitionerId(), "Patient", content);
        }
        else{
            content = "The practitioner cancelled the appointment.";
            send2Patient(connector.getPatientId(), "Practitioner", content);
        }
    }

    public void parseData(JSONObject data){
        String type = data.getString("type");
        connector.setType(type);

        if(type.equals("Update")){
            connector.setPractitionerId(data.getString("practitionerId"));
            connector.setPractitionerName(data.getString("practitionerName"));
        }
        else if(type.equals("Delete")){
            connector.setPatientId(data.getString("patientId"));
            connector.setPatientName(data.getString("patientName"));
        }
        else{
            connector.setPatientId(data.getString("patientId"));
            connector.setPatientName(data.getString("patientName"));
            connector.setPractitionerId(data.getString("practitionerId"));
            connector.setPractitionerName(data.getString("practitionerName"));
        }

    }


    @GetMapping("/fhir/profiles/{resource}/{id}")
    public void send2Patient(@PathVariable String id, @PathVariable String resource, String content){
        String rel = "";
        rel = fhirService.get(resource, id);
        JSONObject data = JSON.parseObject(rel);
        recipient[0] = data.getString("email");

        try {
            // 创建邮件消息
            message.setFrom(new InternetAddress("${email.username}"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient[0]));
            message.setSubject("${email.subject}\"");
            message.setText(content);

            // 发送邮件
            Transport.send(message);

            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            System.out.println("邮件发送失败：" + e.getMessage());
        }
    }

    @GetMapping("/fhir/profiles/{resource}/{id}")
    public void send2Practitioner(@PathVariable String id, @PathVariable String resource, String content){
        String rel = "";
        rel = fhirService.get("Practitioner", id);
        JSONObject data = JSON.parseObject(rel);
        recipient[1] = data.getString("email");

        try {
            // 创建邮件消息
            message.setFrom(new InternetAddress("${email.username}"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient[1]));
            message.setSubject("${email.subject}\"");
            message.setText(content);

            // 发送邮件
            Transport.send(message);

            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            System.out.println("邮件发送失败：" + e.getMessage());
        }
    }
}
