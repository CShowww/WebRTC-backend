package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.service.ProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


/**
 * Basic CURD operation of fhir service
 */
@Service
@CacheConfig(cacheNames = "profiles")
public class ProfilesServiceImpl implements ProfilesService {

    // TODO: cache it
    @Autowired
    HttpFhirService httpFhirService;

    /**
     * Add a fhir resource
     * @param resource
     * @param id
     * @return Resource with fhir allocated id
     */

    @Override
    public R<String> get(String resource, String id) {
        String rel = null;
        try{
            rel = httpFhirService.get(resource, id);
        } catch (Exception e) {

            e.printStackTrace();
            rel = e.getMessage();
            return R.error(rel);
        }
        return R.success(rel);
    }

    /**
     * Add fhir service
     * @param resource
     * @param data
     * @return
     */

    @Override
    public R<String> add(String resource, String data) throws ExecutionException, InterruptedException {
        Callable<String> addToFhir = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String rel = "";
                try {
                    rel = httpFhirService.add(resource, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return rel;
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(addToFhir);
        Thread thread = new Thread(futureTask);
        thread.start();

        // Obtain result
        String rel = futureTask.get();
        JSONObject jsonObject = JSONObject.parseObject(rel);
        String resourceId = jsonObject.getString("id");


        return R.success(rel);
    }

    /**
     * Delete fhir resource
     * @param resource
     * @param id
     * @return
     */
    @Override
    public R<String> delete(String resource, String id) {
        String rel = "";
        try {
            rel = httpFhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            rel = e.getMessage();

            return R.error(rel);
        }
        return R.success(rel);
    }

    /**
     * Update a fhir resource with id
     * @param resource
     * @param id
     * @param data
     * @return
     */
    @Override
    public R<String> update(String resource, String id, String data) {
        String rel = "";
        try {
            rel = httpFhirService.update(resource, id, data);
        } catch (Exception e) {
            e.printStackTrace();

            rel = e.getMessage();

            return R.error(rel);
        }
        return R.success(rel);
    }

    /**
     *
     * @param resource
     * @return
     */
    @Override
    public R<String> getAll(String resource) {
        String rel = "";
        try {
            rel = httpFhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();

            return R.error(rel);
        }
        return R.success(rel);
    }

    /**
     *
     * @param subject
     * @return
     */
    public R<String> getBySubject(String subject) {
        String rel = "";
        try {
            rel = httpFhirService.getBySubject(subject);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();

            return R.error(rel);
        }
        return R.success(rel);
    }

}
