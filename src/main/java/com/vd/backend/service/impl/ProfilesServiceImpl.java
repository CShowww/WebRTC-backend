package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.service.ProfilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.List;

/**
 * Basic CURD operation of fhir service
 */

@Slf4j
@Service
public class ProfilesServiceImpl implements ProfilesService {

    @Autowired
    HttpFhirService httpFhirService;

    @Autowired
    AsynFhirService asynFhirService;


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
            rel = asynFhirService.get(resource, id);
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
        String rel = "";
        try {
            rel = asynFhirService.add(resource, data);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
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
            rel = asynFhirService.delete(resource, id);
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
            rel = asynFhirService.update(resource, id, data);
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
        List<String> resources = new ArrayList<>();
        String rel = null;

        try {
            resources = asynFhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();
            return R.error(rel);
        }

        for (String res : resources) {
            // TODO: get all logic
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
