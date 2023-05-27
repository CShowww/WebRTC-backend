package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.AsyncFhirService;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.service.ProfilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.List;

/**
 * Basic CURD operation of fhir service
 */

@Slf4j
@Service
public class ProfilesServiceImpl implements ProfilesService {

    // Sync method, suffer from latency of fhir server
    @Autowired
    HttpFhirService httpFhirService;

    // Async method, using cache
    @Autowired
    AsyncFhirService asynFhirService;


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

        // Transfer to front end required data format
        JSONArray info = new JSONArray();

        for (String s : resources) {
            JSONObject res = JSON.parseObject(s);
            String family = "", email = "", given = "", id = "";
            JSONObject name = res.getJSONArray("name")
                    .getJSONObject(0);

            for (Object str: name.getJSONArray("given")) {
                given += (String) str + " ";
            }

            family = name.getString("family");
            id = res.getString("id");

            JSONArray telecom = res.getJSONArray("telecom");
            for (int j = 0; j < telecom.size(); j++) {
                JSONObject t = telecom.getJSONObject(j);
                String system = t.getString("system");
                if ("email".equals(system)) {
                    email = t.getString("value");
                }
            }
            JSONObject profilesInfo = new JSONObject();
            profilesInfo.put("family", family);
            profilesInfo.put("given", given);
            profilesInfo.put("contact", email);
            profilesInfo.put("id", id);


            info.add(profilesInfo);
        }

        return R.success(info.toString());
    }

    /**
     * TODO: cache it
     * @param subject
     * @return
     */
    public R<String> getBySubject(String resource, String subject) {

        String rel = "";
        try {
            rel = asynFhirService.getBySubject(resource, subject);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();

            return R.error(rel);
        }
        return R.success(rel);
    }



}
