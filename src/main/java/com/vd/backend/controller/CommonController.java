package com.vd.backend.controller;


import com.vd.backend.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

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

}
