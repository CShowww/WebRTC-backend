package com.vd.backend.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public interface AsyncFhirService {
    String add(String resource, String id) throws ExecutionException, InterruptedException;

    String delete(String resource, String id);

    String update(String resource, String id, String data);

    String get(String resource, String id) throws ExecutionException, InterruptedException, TimeoutException;

    List<String> getAll(String resource);


    String getBySubject(String resource, String subject) throws ExecutionException, InterruptedException, TimeoutException;

    List<String> getByPractitionerId(String resource, String id);

    List<String> getByPatientId(String resource, String id);

}
