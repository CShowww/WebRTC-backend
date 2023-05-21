package com.vd.backend.service.impl;

import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.RemoteFhirService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class AsynRemoteFhirServiceImpl implements AsynFhirService {

    // Remote http call to fhir service
    @Autowired
    private RemoteFhirService remoteFhirService;

    private ConcurrentHashMap<String, CacheImpl> resourceCache = new ConcurrentHashMap<>();

    private List<String> resources = new ArrayList<>(
            Arrays.asList("Patient", "Observation", "Appointment", "Practitioner"));


    // load fhir resource into cache
    @PostConstruct
    void init() {


    }

    @Override
    public void add() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void update() {

    }

    @Override
    public void get() {

    }
}
