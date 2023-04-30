package com.example.webrtcbackend.controller;


import com.example.webrtcbackend.common.R;
import com.example.webrtcbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @Value("${common.basepath}")
    String basePath;

    /**
     * 登陆服务
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/login")
    public R<String> login(HttpServletRequest httpServletRequest) {
        log.info("Save Token");

        userService.saveToken(httpServletRequest);

        String userId = httpServletRequest.getParameter("userId");

        httpServletRequest.getSession().setAttribute("userId", userId);

        return R.success();
    }

    /**
     * 文件上传服务 pdf?
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile[] files, @RequestParam("category") String category, HttpServletRequest request) {
        log.info("user upload {} file(s)", files.length);

        // rename by timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH:mm:ss"));

        // get running jar dir
        ApplicationHome home = new ApplicationHome(getClass());

        for (int i=0; i< files.length; i++) {
            MultipartFile file = files[i];

            String fileNameSplit[] = file.getOriginalFilename()
                    .split("\\.");

            String newName = fileNameSplit[0] + timestamp + "." + fileNameSplit[1];

            File targetPath = new File(home.getDir().getPath() + basePath + request.getParameter("userId") + "/" + category + "/");

            if (!targetPath.exists()) {
                targetPath.mkdirs();
            }
            // file transfer
            try {
                file.transferTo(new File(targetPath.getPath() + "/" + newName));
                log.info("file transfer to " + targetPath);
            } catch (IOException e) {
                e.printStackTrace();

                return R.error("upload error: " + e.getMessage());
            }
        }

        return R.success("Upload success");
    }



    // TODO, 下载，返回最晚创建的文件
    // TODO, 上传健康数据，先Append到csv文件中
    // 然后直接下载
    /**
     * 文件下载服务
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam("fileType") String fileType) {
        log.info("hi");

    }

}
