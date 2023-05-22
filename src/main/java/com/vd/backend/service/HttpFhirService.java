package com.vd.backend.service;

import com.vd.backend.config.FeignConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        value="${fhir.name}",
        url="${fhir.url}",
        configuration = FeignConfig.class
)

@Primary
public interface HttpFhirService {
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

    @RequestMapping(value="/Observation")
    public String getBySubject(@RequestParam("subject") String subject);

    @RequestMapping(value = "/{resource}?practitioner={id}", method = RequestMethod.GET, headers = "content-type=application/fhir+json")
    public String getById(@PathVariable String resource, @PathVariable String id);

}
