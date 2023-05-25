package com.vd.backend.service;

public interface ObservationService {

    public String formatBundle(String data);

    public String formatToObservation(String resource, String id, String data);
}
