package com.vd.backend.controller;


import com.vd.backend.common.R;
import com.vd.backend.service.ProfilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;


/**
 * CURD of Fhir resource
 */
@Slf4j
@RestController
@RequestMapping("/profiles")
@CrossOrigin
public class ProfilesController {

    @Autowired
    private ProfilesService profilesService;


    /**
     * Get Patient resource by id
     * @param id
     * @param
     * @return
     */

    @GetMapping("/{resource}/{id}")
    public R<String> get(@PathVariable String resource, @PathVariable String id) throws ExecutionException, InterruptedException {

        return profilesService.get(resource, id);
    };






    /**
     * Get all resource
     * @param
     * @return
     */
    @GetMapping("/{resource}")
    public R<String> getAll(@PathVariable String resource) {

        return profilesService.getAll(resource);
    }

    @PutMapping("/{resource}/{id}")
    public R<String> update(@PathVariable String resource, @PathVariable String id, @RequestBody String data) {

        return profilesService.update(resource, id, data);
    }

    /**
     * Delete resource with id
     * @param
     * @param id
     * @return
     */
    @DeleteMapping("/{resource}/{id}")
    public R<String> delete(@PathVariable String resource, @PathVariable String id) {


        return profilesService.delete(resource, id);
    }

    /**
     * Add resource with id
     * @param resource
     * @param data
     * @return
     */
    @PostMapping("/{resource}")
    public R<String> add(@PathVariable String resource, @RequestBody String data) throws ExecutionException, InterruptedException {

        return profilesService.add(resource, data);
    }
}
