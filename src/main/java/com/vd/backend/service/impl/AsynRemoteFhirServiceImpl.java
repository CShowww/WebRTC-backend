package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.Cache;
import com.vd.backend.service.RemoteFhirService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
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
        // Get the most of resources into cache
        for(String resource: resources) {
            String fhirRawData;
            try {
                fhirRawData = remoteFhirService.getAll(resource);
                loadToCache(fhirRawData, resource);
            } catch (Exception e) {
                log.info("Calling remote fhir service fail");
                e.printStackTrace();
            }
        }
    }

    @Override
    public String add(String resource, String data) throws ExecutionException, InterruptedException {
        // add fhir server
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String rel = "";
                try {
                    rel = remoteFhirService.add(resource, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject jsonObject = JSONObject.parseObject(rel);

                String id = jsonObject.getString("id");

                return id;
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(task);
        Thread thread = new Thread(futureTask);
        thread.start();

        String resourceId = futureTask.get();

        // update cache
        this.resourceCache.get(resource).put(resourceId, resource);

        return " add ok";
    }

    @Override
    public void delete() {

    }

    @Override
    public void update() {

        // update cahce

        // update fhir service
        // thread.start()

    }

    @Override
    public void get() {

    }


    private void loadToCache(String data, String resource) {

        CacheImpl cache = new CacheImpl(50);

        // json process
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray entry = jsonObject.getJSONArray("entry");

        for(int i=0; i < entry.size(); i++) {

            JSONObject entryObject = entry.getJSONObject(i);

            JSONObject resourceObject = entryObject.getJSONObject("resource");

            String id = resourceObject.getString("id");

            cache.put(id, resourceObject.toString());
        }

        resourceCache.put(resource, cache);

    }

}
