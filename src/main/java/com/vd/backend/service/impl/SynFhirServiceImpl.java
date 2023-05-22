package com.vd.backend.service.impl;

import com.vd.backend.service.HttpFhirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SynFhirServiceImpl {
    @Autowired
    HttpFhirService httpFhirService;

    public String get(String resource, String id) {
        String rel = null;
        try{
            rel = httpFhirService.get(resource, id);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();
        }
        return rel;
    }

    public String getAll(String resource) {
        String rel = "";
        try {
            rel = httpFhirService.getAll(resource);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();
        }
        return rel;
    }

    public String add(String resource, String data) {
        String rel = "";
        try {
            rel = httpFhirService.add(resource, data);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();
        }

        return rel;
    }

    public String update(String resource, String id, String data) {
        String rel = "";
        try {
            rel = httpFhirService.update(resource, id, data);
        } catch (Exception e) {
            e.printStackTrace();

            rel = e.getMessage();
        }

        return rel;
    }

    public String delete(String resource, String id) {
        String rel = "";
        try {
            rel = httpFhirService.delete(resource, id);
        } catch (Exception e) {
            e.printStackTrace();

            rel = e.getMessage();
        }

        return rel;
    }

    public String getBySubject(String subject) {
        String rel = "";
        try {
            rel = httpFhirService.getBySubject(subject);
        } catch (Exception e) {
            e.printStackTrace();

            rel = e.getMessage();
        }
        return rel;
    }

    public String getById(String resource, String id) {
        String rel = "";
        try {
            rel = httpFhirService.getById(resource, id);
        } catch (Exception e) {
            e.printStackTrace();
            rel = e.getMessage();
        }
        return rel;
    }


}
