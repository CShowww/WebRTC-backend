package com.vd.backend.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.entity.vo.UserInfo;
import com.vd.backend.service.KeycloakService;
import com.vd.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    KeycloakService keycloakService;

    @Value("${common.basepath}")
    String basePath;

    /**
     * 登陆服务
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody String data, HttpServletRequest httpServletRequest ) {
        log.info("Save Token");

        String res = userService.saveToken(httpServletRequest, data);

        if(res==null){
            return R.error("Create Resource fail!");
        }

        return R.success(res);
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
                log.info("file transfer to " + targetPath.getPath() + "/" + newName);
            } catch (IOException e) {
                e.printStackTrace();

                return R.error("upload error: " + e.getMessage());
            }
        }

        return R.success("Upload success");
    }

    @GetMapping("/files/{id}")
    public R<String> getFiles(@PathVariable String id, @RequestParam("category") String category) {

        String folderPath = new ApplicationHome(getClass()).getDir().getPath() + basePath + id + "/" + category + "/";

        File folder = new File(folderPath);
        JSONArray ans = new JSONArray();
        if (folder.isDirectory()) {

            File[] files = folder.listFiles();
            for (File file : files) {
                JSONObject jsonObject = new JSONObject();

                if (file.isFile()) {
                    log.info(file.toString());
                    jsonObject.put("fieldName", file.getName());
                }
                ans.add(jsonObject);
            }



        }
        return R.success(ans.toString());
    }





    // TODO, 下载，返回最晚创建的文件
    // TODO, 上传健康数据，先Append到csv文件中
    // 然后直接下载
    /**
     * 文件下载服务
     */

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id, @RequestParam("category") String category, @RequestParam("filename") String filename) throws IOException {
        log.info("download {}", id);
        String folderPath = new ApplicationHome(getClass()).getDir().getPath() + basePath + id + "/" + category + "/";

//        File newestFile = getNewestFile(folderPath);

        File newestFile = new File(folderPath + filename);

        log.info(newestFile.getPath());

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
        log.info("path: ", folderPath);
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            return null;
        }

        return folder.listFiles()[0];
    }

    private String getFileContentType(File file) throws IOException {
        return Files.probeContentType(file.toPath());
    }

    @PostMapping("/hi")
    public R<String> upload() {
        log.info("hi");
        return R.success("hi");
    }

    @PostMapping("/add")
    public R<String> addUser(@RequestBody UserInfo userInfo) {
        String token = "";
        try{
            token = keycloakService.getAccessToken();
        }catch (HttpClientErrorException e){
            return R.error("Get token fail.");
        }


        if(token==null){
            return R.error("Token is null");
        }

        try{
            keycloakService.addUser(userInfo, token);
        }catch (HttpClientErrorException e){
            return R.error(e.getMessage());
        }

        return R.success("Success");

    }

    @PutMapping("/update/{id}")
    public R<String> updateUser(@PathVariable String id, @RequestBody UserInfo userInfo) {
        String token = "";
        try{
            token = keycloakService.getAccessToken();
        }catch (HttpClientErrorException e){
            return R.error("Get token fail.");
        }


        if(token==null){
            return R.error("Token is null");
        }

        try{
            keycloakService.update(userInfo, token, id);
        }catch (HttpClientErrorException e){
            return R.error(e.getMessage());
        }

        return R.success("Success");

    }

}
