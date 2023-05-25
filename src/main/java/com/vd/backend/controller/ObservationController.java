package com.vd.backend.controller;


import com.vd.backend.common.R;
import com.vd.backend.service.ObservationService;
import com.vd.backend.service.ProfilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("observation")
@CrossOrigin
public class ObservationController {

    @Autowired
    ObservationService observationService;

    @Autowired
    ProfilesService profilesService;

    private final String resource = "Observation";


    @GetMapping("/{subject}/{id}")
    public R<String> getSummary( @PathVariable String subject, @PathVariable String id) {

        // 1. Get all observation with subject id
        String bundle = profilesService.getBySubject(resource, subject  + "/" + id).getData();

        // 2. Formatting bundled data and return to frontend
        String formattedData = observationService.formatBundle(bundle);

        return R.success(formattedData);
    }


    @PostMapping("/{resource}/{id}")
    public R<String> addObservation(@PathVariable String resource, @PathVariable String id, @RequestBody String data) {

        String rel = observationService.formatToObservation(resource, id, data);

        return R.success(rel);
    }
}















