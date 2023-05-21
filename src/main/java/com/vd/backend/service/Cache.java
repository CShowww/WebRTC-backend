package com.vd.backend.service;

public interface Cache {

    // Get resource and return
    void get(String key);

    // Put updated resource
    void put(String key, String val);
}
