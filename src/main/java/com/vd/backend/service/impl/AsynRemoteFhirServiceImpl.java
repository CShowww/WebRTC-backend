package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.RemoteFhirService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
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
        Callable<String> addToFhir = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String rel = "";
                try {
                    rel = remoteFhirService.add(resource, data);
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

        // update cache
        this.resourceCache.get(resource).put(resourceId, resource);

        return rel;
    }

    @Override
    public String delete() {
        return null;
    }

    @Override
    public String update(String resource, String id, String data) {
        // update cache
        this.resourceCache.get(resource).put(id, resource);

        // update fhir service
        Thread updateToFhir = new Thread(new Runnable() {
            @Override
            public void run() {
                String rel = "";
                try {
                    rel = remoteFhirService.update(resource, id, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        updateToFhir.start();

        return "Asyn updating";
    }

    @Override
    public String get(String resource, String id) throws ExecutionException, InterruptedException {
        Callable<String> getFromFhir = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String rel = "";
                try{
                    rel = remoteFhirService.get(resource, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return rel;
            }
        };

        FutureTask<String> task = new FutureTask<>(getFromFhir);
        String rel = "";

        // Get from database if not exist
        if (this.resourceCache.get(resource).containsKey(id)) {
            rel = this.resourceCache.get(resource).get(id);
        } else {
            rel = task.get();
            this.add(resource, rel);
        }

        return rel;
    }

    @Override
    public String getAll(String resource) {
        Thread getAllFromFhir = new Thread(new Runnable() {
            @Override
            public void run() {
                String rel = "";
                try {
                    rel = remoteFhirService.getAll(resource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TODO update cache
            }
        });

        String rel = resourceCache.get(resource).toString();
        return rel;
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
