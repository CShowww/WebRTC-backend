package com.vd.backend.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public interface AsynFhirService {
    String add(String resource, String id) throws ExecutionException, InterruptedException;

    String delete(String resource, String id);

    String update(String resource, String id, String data);

    String get(String resource, String id) throws ExecutionException, InterruptedException, TimeoutException;

    List<String> getAll(String resource);


    String getBySubject(String resource, String subject) throws ExecutionException, InterruptedException, TimeoutException;

    String getByPractitionerId(String resource, String id);

    String getByPatientId(String resource, String id);

}
