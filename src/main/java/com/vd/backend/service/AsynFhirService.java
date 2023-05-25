package com.vd.backend.service;

import java.util.concurrent.ExecutionException;



public interface AsynFhirService {
    String add(String resource, String id) throws ExecutionException, InterruptedException;

    String delete();

    String update(String resource, String id, String data);

    String get(String resource, String id) throws ExecutionException, InterruptedException;

    String getAll(String resource);
}
