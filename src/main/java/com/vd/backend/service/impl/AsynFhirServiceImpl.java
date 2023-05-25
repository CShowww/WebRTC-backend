package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.CacheService;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.util.CacheImpl;
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
public class AsynFhirServiceImpl implements AsynFhirService {

    // Remote http call to fhir service
    @Autowired
    private HttpFhirService httpFhirService;

    private ConcurrentHashMap<String, CacheImpl> resourceCache = new ConcurrentHashMap<>();


    @Autowired
    CacheService cacheService;


    private List<String> resources = new ArrayList<>(
            Arrays.asList("Patient", "Observation", "Appointment", "Practitioner"));


    /**
     * Add to fhir service and cache it
     * @param resource
     * @param data
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public String add(String resource, String data) throws ExecutionException, InterruptedException {
        // Add to fhir server and obtain resource id
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

        String rel = futureTask.get();
        String id = JSONObject.parseObject(rel).getString("id");

        // update cache
        cacheService.set(getCacheKey(resource, id), data);

        return rel;
    }

    @Override
    public String delete() {
        return null;
    }

    @Override
    public String update(String resource, String id, String data) {

        // update cache
        cacheService.set(getCacheKey(resource, id), data);

        // Async update fhir service
        Thread updateToFhir = new Thread(new Runnable() {
            @Override
            public void run() {
                String rel = "";
                try {
                    rel = httpFhirService.update(resource, id, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        updateToFhir.start();

        return "Asyn updating";
    }

    /**
     * TODO: check consistency
     * @param resource
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public String get(String resource, String id) throws ExecutionException, InterruptedException {
        Callable<String> getFromFhir = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String rel = "";
                try{
                    rel = httpFhirService.get(resource, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return rel;
            }
        };

        FutureTask<String> task = new FutureTask<>(getFromFhir);
        String rel = "";

        // Get from database if not exist

        if (cacheService.hasKey(getCacheKey(resource, id))) {
            rel = cacheService.get(getCacheKey(resource, id));
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
                    rel = httpFhirService.getAll(resource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TODO update cache
            }
        });

        String rel = resourceCache.get(resource).toString();
        return rel;
    }


    /**
     * Load all available resource into cache
     */
    @PostConstruct
    void init() {
        for(String resource: resources) {

            try {
                String bundle = httpFhirService.getAll(resource);
                loadToCache(bundle, resource);
            } catch (Exception e) {
                log.info("Calling remote fhir service fail");
                e.printStackTrace();
            }
        }
    }




    private void loadToCache(String bundle, String resource) {
        // cache bundle data
        cacheService.set(getCacheKey(resource, "bundle"), bundle);

        // json process
        JSONObject jsonObject = JSONObject.parseObject(bundle);
        JSONArray entry = jsonObject.getJSONArray("entry");

        for (int i=0; entry != null && i < entry.size(); i++) {

            JSONObject res =  entry.getJSONObject(i).getJSONObject("resource");

            String id = res.getString("id");

            String cacheId = getCacheKey(resource, id);

            String cacheData = res.toJSONString();

            cacheService.set(cacheId, cacheData);
        }
    }


    private String getCacheKey(String resource, String id) {
        return resource + ":" + id;
    }

}
