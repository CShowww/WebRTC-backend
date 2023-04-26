package com.example.webrtcbackend.controller;


import com.example.webrtcbackend.common.R;
import com.example.webrtcbackend.service.FhirService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/fhir")
@CrossOrigin
public class FhirController {
    @Autowired
    private FhirService fhirService;

    @GetMapping("/{resource}/{id}")
    public R<String> get(@PathVariable String id, @PathVariable String resource, HttpServletRequest request) {
        System.out.println("GET Fhir");
        log.info("GET Fhir logging");
        String rel = "";
        try{
            rel = fhirService.get(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("Call fhir server fail");
        }
        return R.success(rel);
    }
    @GetMapping("/{resource}")
    public R<String> getAll(@PathVariable String resource) {
        log.info("GET ALL Fhir");
        String rel = "";
        try {
            rel = fhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }
        return R.success(rel);
    }


    @PutMapping("/{resource}/{id}")
    public R<String> update(@PathVariable String resource, @PathVariable String id, @RequestBody String data) {
        log.info("UPDATE");
        String rel = "";
        try {
            rel = fhirService.update(resource, id, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("UPDATE fail");
        }
        return R.success(rel);
    }


    @DeleteMapping("/{resource}/{id}")
    public R<String> delete(@PathVariable String resource, String id) {
        String rel = "";
        try {
            rel = fhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("DELETE fail");
        }

        return R.success(rel);
    }

    @PostMapping ("/{resource}")
    public R<String> add(@PathVariable String resource, @RequestBody String data) {
        String rel = "";
        try {
            rel = fhirService.add(resource, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("UPDATE fail");
        }
        return R.success(rel);
    }

    // Get Fhir resource Identifier
    private String getFhirResource(String uri){
        String[] path = uri.split("/");
        int index = IntStream.range(0, path.length)
                .filter(i -> path[i].equals("fhir"))
                .findFirst()
                .orElse(-1) - 1;
        return path[index];
    }

}


