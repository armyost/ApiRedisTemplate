package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class DemoLookAsideController {

    private final DemoLookAsideService demoLookAsideService;

    public DemoLookAsideController(DemoLookAsideService demoLookAsideService) {
        this.demoLookAsideService = demoLookAsideService;
    }

    private static final Logger logger = LoggerFactory.getLogger(DemoLookAsideController.class);;

    @GetMapping(value = "/ping")
    public String getPing() {
        String response_val = "pong";
        return response_val;
    }

    /*
     * Look-Aside Caching pattern Test
     */

    // Cacheable Controller
    @GetMapping(value = "/cacheablePath/{postValue}")
    public String cacheablePath(@PathVariable String postValue) {
        String responseValue = "EMPTY_VALUE";
        
        HashMap<String, String> sampleMap = new HashMap<String, String>();
        sampleMap.put("SampleKey",postValue);

        // realApplication <- Path will be cached
        this.demoLookAsideService.getRestData("http://localhost:8080", "/realApplication/listSample.do", sampleMap, HttpMethod.POST);
        return responseValue;
    }

    // Uncacheable Controller
    @GetMapping(value = "/uncacheablePath/{postValue}")
    public String uncacheablePath(@PathVariable String postValue) {
        String responseValue = "EMPTY_VALUE";

        HashMap<String, String> sampleMap = new HashMap<String, String>();
        sampleMap.put("SampleKey",postValue);

        // demoApplication <- Path will be uncached
        this.demoLookAsideService.getRestData("http://localhost:8080", "/demoApplication/listSample.do", sampleMap, HttpMethod.POST);
        return responseValue;
    }


    /* SAMPLE -------------------------------------------------------------------

    @GetMapping(value = "/{postId}")
    public DemoEntity findGet(@PathVariable String postId){
        return demoEntity;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPost(@RequestBody Map<String, String> requestBody){
        return new ResponseEntity<>("result successful", HttpStatus.OK);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> modifyPut(@PathVariable String postId, @RequestBody Map<String, String> requestBody){
        return new ResponseEntity<>("result successful", HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> removeDelete(@PathVariable String postId){
        return new ResponseEntity<>("result successful", HttpStatus.OK);
    }
    ------------------------------------------------------------------------------- */
}