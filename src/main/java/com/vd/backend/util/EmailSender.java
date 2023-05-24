package com.vd.backend.util;


import com.alibaba.fastjson.JSONObject;
import com.vd.backend.entity.bo.Connector;
import lombok.extern.slf4j.Slf4j;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
public class EmailSender {
    private Connector connector;
    private Authenticator auth;
    private Session session;
    private Message message;
    private String content;

    public void init(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("CShowwww@gmail.com", "shsrfiurpnshlyrv");
            }
        };

        session = Session.getInstance(props, auth);
        message = new MimeMessage(session);
    }

    public void sendEmail(JSONObject data) {
        parseData(data);
        init();
        log.info(connector.toString());

        if(connector.getType().equals("Add")){
            String contentForPractitioner = connector.getPatientName()+" made an appointment!";
            String contentForPatient = "You have made the appointment successfully!";

            send2Patient(contentForPatient);
            send2Practitioner(contentForPractitioner);
        }
        else if(connector.getType().equals("Update")){
            content = "The patient updated the appointment.";
            send2Practitioner(content);
        }
        else{
            content = "The practitioner cancelled the appointment.";
            send2Patient(content);
        }
    }

    public void parseData(JSONObject data){
        String type = data.getString("type");
        connector = new Connector();
        connector.setType(type);

        if(type.equals("Update")){
            connector.setPractitionerId(data.getString("practitionerId"));
            connector.setPractitionerName(data.getString("practitionerName"));
            connector.setPractitionerEmail(data.getString("practitionerEmail"));
        }
        else if(type.equals("Delete")){
            connector.setPatientId(data.getString("patientId"));
            connector.setPatientName(data.getString("patientName"));
            connector.setPatientEmail(data.getString("patientEmail"));

        }
        else{
            connector.setPatientId(data.getString("patientId"));
            connector.setPatientName(data.getString("patientName"));
            connector.setPatientEmail(data.getString("patientEmail"));
            connector.setPractitionerId(data.getString("practitionerId"));
            connector.setPractitionerName(data.getString("practitionerName"));
            connector.setPractitionerEmail(data.getString("practitionerEmail"));
        }

    }

    public void send2Patient(String content){
        log.info("Send email to patient, the content is {}",content);
        try {
            message.setFrom(new InternetAddress("${email.username}"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(connector.getPatientEmail()));
            message.setSubject("Notification from Virtual Doctor Platform");
            message.setText(content);

            Transport.send(message);

            log.info("Send successfully!");
        } catch (MessagingException e) {
            log.info("Fail to send email." + e.getMessage());
        }
    }

    public void send2Practitioner(String content){
        log.info("Send email to practitioner, the content is {}",content);
        try {
            message.setFrom(new InternetAddress("${email.username}"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(connector.getPractitionerEmail()));
            message.setSubject("Notification from Virtual Doctor Platform");
            message.setText(content);

            Transport.send(message);

            log.info("Send successfully!");
        } catch (MessagingException e) {
            log.info("Fail to send email." + e.getMessage());
        }
    }
}
