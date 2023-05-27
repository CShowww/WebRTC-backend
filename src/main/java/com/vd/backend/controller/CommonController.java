package com.vd.backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.vd.backend.common.R;
import com.vd.backend.service.HttpFhirService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Discard
 *
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    HttpFhirService fhirService;


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.getOriginalFilename().toString());

        String originalFilename = file.getOriginalFilename();//abc.jpg

        return R.success("any");
    }

    @DeleteMapping("/deleteAll/{resource}")
    public R<String> deleteAll(@PathVariable String resource) {
        log.info("Delete all {}", resource);

        String rel = "";
        try {
            rel = fhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");
        for (int i = 0; i<entry.size(); i++){
            try {
                rel = fhirService.delete(resource, entry.getJSONObject(i).getJSONObject("resource").getString("id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return R.success();
    }
}
