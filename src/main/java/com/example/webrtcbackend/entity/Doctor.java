package com.example.webrtcbackend.entity;


import lombok.Data;
import java.io.Serializable;

@Data
public class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    private Integer role;
}
