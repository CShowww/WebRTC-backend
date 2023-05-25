package com.vd.backend.service;

public interface ObservationService {

    public String getSummary(String data);

    public String addObservation(String resource, String id, String data);
}
