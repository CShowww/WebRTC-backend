package com.vd.backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.backend.common.R;
import com.vd.backend.service.AsynFhirService;
import com.vd.backend.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/profiles")
@CrossOrigin
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     *  profiles: return fhir resources
     */
    @GetMapping("/{resource}/{id}")
    public R<String> get(@PathVariable String id, @PathVariable String resource, HttpServletRequest request) throws ExecutionException, InterruptedException {
        log.info("Get {}/{}", resource, id);

        String rel = patientService.get(resource, id);

        return R.success(rel);
    }


    @GetMapping("/{resource}")
    public R<String> getAll(@PathVariable String resource) {
        log.info("Get all {}", resource);
        String rel = "";

        try {
            rel = remoteFhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("Call fhir server fail");
        }


        JSONArray entry = JSON.parseObject(rel).getJSONArray("entry");

        JSONArray ans = new JSONArray();

        for (int i = 0; i < entry.size(); i++) {
            JSONObject jsonObject = new JSONObject();

            String family = "", phone = "", given = "";

            JSONObject res = entry.getJSONObject(i).getJSONObject("resource");
            JSONObject name = res.getJSONArray("name")
                    .getJSONObject(0);
            String id = res.getString("id");


            family = name.getString("family");
            for (Object s: name.getJSONArray("given")) {
                given += (String) s + " ";
            }

            JSONArray telecom = res.getJSONArray("telecom");
            for (int j = 0; j < telecom.size(); j++) {
                JSONObject t = telecom.getJSONObject(j);
                String system = t.getString("system");
                if ("phone".equals(system)) {
                    phone = t.getString("value");
                }
            }
            jsonObject.put("family", family);
            jsonObject.put("given", given);
            jsonObject.put("contact", phone);
            jsonObject.put("id", id);

            ans.add(jsonObject);
        }


        return R.success(ans.toString());
    }


    @PutMapping("/{resource}/{id}")
    public R<String> update(@PathVariable String resource, @PathVariable String id, @RequestBody String data) {
        log.info("Update {}/{}", resource, id);
        String rel = "";
        try {
            rel = remoteFhirService.update(resource, id, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("UPDATE fail");
        }
        return R.success(rel);
    }


    @DeleteMapping("/{resource}/{id}")
    public R<String> delete(@PathVariable String resource, @PathVariable String id) {
        log.info("Delete {}/{}", resource, id);
        String rel = "";
        try {
            rel = remoteFhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error("DELETE fail");
        }

        return R.success(rel);
    }

    @PostMapping ("/{resource}")
    public R<String> add(@PathVariable String resource, @RequestBody String data) {
        log.info("Post {}", resource);
        String rel = "";
        try {
            rel = remoteFhirService.add(resource, data);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }

        JSONObject jsonObject = JSONObject.parseObject(rel);
        log.info(jsonObject.toString() +" " + jsonObject.getObject("id", String.class));
        String pId = jsonObject.getString("id");
        R<String> r = R.success(rel);
        r.setMsg(pId);
        return r;
    }

}
