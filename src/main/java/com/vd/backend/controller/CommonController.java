package com.vd.backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.vd.backend.common.R;
import com.vd.backend.service.FhirService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    FhirService fhirService;

    /**
     * 文件上传
     *
     * @param file
     * @return 文件上传的目录改为项目运行的根目录
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.getOriginalFilename().toString());

        //原始文件名
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
