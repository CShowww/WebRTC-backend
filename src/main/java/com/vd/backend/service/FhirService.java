package com.example.webrtcbackend.service;

import com.example.webrtcbackend.config.FeignConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        value="${fhir.name}",
        url="${fhir.url}",
        configuration = FeignConfig.class
)

@Primary
public interface FhirService {
    // RESTful CRUD Operation
    @RequestMapping(value = "/{resource}/{id}", method = RequestMethod.GET, headers = "content-type=application/fhir+json")
    public String get(@PathVariable String resource, @PathVariable String id);

    @RequestMapping(value = "/{resource}", method = RequestMethod.GET, headers = "content-type=application/fhir+json")
    public String getAll(@PathVariable String resource);

    @RequestMapping(value = "/{resource}", method = RequestMethod.POST, headers = "content-type=application/fhir+json")
    public String add(@PathVariable String resource, String data);

    @RequestMapping(value = "/{resource}/{id}", method = RequestMethod.PUT, headers = "content-type=application/fhir+json")
    public String update(@PathVariable String resource,@PathVariable String id, @RequestBody String data);

    @RequestMapping(value = "/{resource}/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String resource, @PathVariable String id);
}
