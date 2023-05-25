package com.vd.backend.service;


import com.vd.backend.common.R;

import java.util.concurrent.ExecutionException;

/**
 * CURD of any resource from fhir
 */
public interface ProfilesService {

    R<String> add(String resource, String data) throws ExecutionException, InterruptedException;

    R<String> delete(String resource, String id);

    R<String> update(String resource, String id, String data);

    R<String> get(String resource, String id);

    R<String> getAll(String resource);

    R<String> getBySubject(String subject);

}
