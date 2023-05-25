package com.vd.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.CacheService;
import com.vd.backend.service.HttpFhirService;
import com.vd.backend.util.CacheImpl;
import com.vd.backend.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AsyncFhirServiceImpl implements AsynFhirService {

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

        // add to fhir and get allocated resource id
        Callable<String> addToFhirTask = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String rel = "";
                try {
                    rel = httpFhirService.add(resource, data);
                } catch (Exception e) {
                    e.printStackTrace();
                    rel = e.getMessage();
                }

                return rel;
            }
        };
        FutureTask<String> future = new FutureTask<>(addToFhirTask);
        Thread thread = new Thread(future);
        thread.start();


        // Obtain result from fhir
        String rel = future.get();
        if (JsonUtil.isResource(rel)) {
            String id = JSONObject.parseObject(rel).getString("id");
            log.info("Add to fhir service, allocated resource id: {}", id);

            // update cache
            cacheService.set(getCacheKey(resource, id), rel);

            // update related

        }



        return rel;
    }

    /**
     *
     * @param resource
     * @param id
     * @return
     */
    @Override
    public String delete(String resource, String id) {

        // Remove from cache
        if (cacheService.hasKey(getCacheKey(resource, id))) {
            cacheService.del(getCacheKey(resource, id));
        }

        // Remove from fhir server
        Thread removeFromFhir = new Thread(new Runnable() {
            @Override
            public void run() {
                String rel = "";
                try {
                    rel = httpFhirService.delete(resource, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        removeFromFhir.start();

        return "remove resource: " + resource + " id: " + id;
    }

    @Override
    public String update(String resource, String id, String data) {
        // backup old data
        String oldData = cacheService.get(getCacheKey(resource, id));

        // update cache
        cacheService.set(getCacheKey(resource, id), data);


        // Async update fhir service
        Thread updateToFhir = new Thread(new Runnable() {
            @Override
            public void run() {
                String rel = null;
                try {
                    rel = httpFhirService.update(resource, id, data);
                } catch (Exception e) {
                    e.printStackTrace();
                    rel = e.getMessage();
                }

                // if fail, rollback to old data
                if (!JsonUtil.isResource(rel)) {
                    cacheService.set(getCacheKey(resource, id), oldData);
                }
            }

        });

        updateToFhir.start();

        return "update " + resource + " " + id;
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
    public String get(String resource, String id) throws ExecutionException, InterruptedException, TimeoutException {

        Callable<String> getFromFhir = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String rel = "";
                try{
                    rel = httpFhirService.get(resource, id);
                } catch (Exception e) {
                    e.printStackTrace();

                    rel = e.getMessage();
                }
                return rel;
            }
        };

        FutureTask<String> task = new FutureTask<>(getFromFhir);
        Thread t = new Thread(task);
        t.start();


        // Obtain result
        String rel = "";
        // Get from fhir database if not exist and update cache if success
        if (cacheService.hasKey(getCacheKey(resource, id))) {
            rel = cacheService.get(getCacheKey(resource, id));
        } else {
            rel = task.get(3000, TimeUnit.MILLISECONDS);
            if (JsonUtil.isResource(rel)) {
                cacheService.set(getCacheKey(resource, id), rel);
            }
        }

        return rel;
    }


    @Override
    public List<String> getAll(String resource) {
        // Get from cache
        List<String> resources = cacheService.getValueByPrefix(resource);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String rel = null;
                try {
                    rel = httpFhirService.getAll(resource);
                    loadToCache(rel, resource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

        return resources;
    }

    /**
     * Return everything of a resource with subject
     * @param resource
     * @param subject
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    @Override
    public String getBySubject(String resource, String subject) throws ExecutionException, InterruptedException, TimeoutException {

        // 1. Gather from cache
        List<String> resources = cacheService.getValueByPrefix(resource);

        // 2. filter with subject
        List<String> rel = resources.stream().filter(e -> {
            JSONObject sub = JSON.parseObject(e).getJSONObject("subject");
            return sub.getString("reference").equals(subject);
        }).collect(Collectors.toList());


        return rel.toString();
    }


    @Override
    public String getByPractitionerId(String resource, String id) {
        return null;
    }

    @Override
    public String getByPatientId(String resource, String id) {
        return null;
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