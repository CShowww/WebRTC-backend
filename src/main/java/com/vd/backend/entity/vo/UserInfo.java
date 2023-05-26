package com.vd.backend.entity.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Array;
import java.util.List;

@Data
public class UserInfo {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @Value("true")
    private String enabled;
    private String username;
    private JSONObject attributes;
    @NotNull
    private JSONArray credentials;
    @NotNull
    private List<String> groups;
}
