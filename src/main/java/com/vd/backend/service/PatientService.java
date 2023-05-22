package com.vd.backend.service;

import java.util.concurrent.ExecutionException;

public interface PatientService {

    String add(String resource, String id);

    String delete();

    String update(String resource, String id, String data);

    String get(String resource, String id);

    String getAll(String resource);

}
