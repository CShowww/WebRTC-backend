package com.vd.backend.controller;


import com.vd.backend.common.R;
import com.vd.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

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
     * @param files
     * @return
     */
    @PostMapping("/upload/{id}")
    public R<String> upload(MultipartFile[] files, @RequestParam("category") String category, @PathVariable String id) {
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

            File targetPath = new File(home.getDir().getPath() + basePath + id + "/" + category + "/");

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
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id, @RequestParam("category") String category) throws IOException {
        String folderPath = new ApplicationHome(getClass()).getDir().getPath() + basePath + id + "/" + category + "/";

        File newestFile = getNewestFile(folderPath);
        if (newestFile == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] fileContent = Files.readAllBytes(newestFile.toPath());
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(getFileContentType(newestFile)));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(newestFile.getName())
                .build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    private File getNewestFile(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            return null;
        }
        return Arrays.stream(folder.listFiles())
                .filter(file -> file.isFile())
                .max(Comparator.comparingLong(file -> file.lastModified()))
                .orElse(null);
    }

    private String getFileContentType(File file) throws IOException {
        return Files.probeContentType(file.toPath());
    }

}
