package com.vd.backend.service;

import java.util.concurrent.ExecutionException;

public interface AsynFhirService {
    String add(String resource, String id) throws ExecutionException, InterruptedException;

    String delete();

    String update();

    String get();
}
