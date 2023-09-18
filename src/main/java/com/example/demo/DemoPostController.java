package com.example.demo;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class DemoPostController {

    @Autowired
    private DemoService demoService;
    private static final Logger logger = LoggerFactory.getLogger(DemoPostController.class);;

    @GetMapping(value = "/ping")
    public String getPing() {
        String response_val = "pong";
        logger.info("!!! ping has been called !!!");
        return response_val;
    }

    // Cacheable Controller
    @GetMapping(value = "/cachePath/test.do")
    public String cachePath() {
        String response_val = "cachePath Called";
        logger.info("!!! cachePath has been called !!!");

        // This is temporary map value. You can test change this value as new one
        HashMap<String, String> testMap = new HashMap<String, String>();
        testMap.put("key2", "두번째값입니다.");
        
        // realApplication <- Path will be cached
        demoService.getRestData("http://localhost:8080", "/realApplication/listSample.do", testMap, HttpMethod.POST);
        return response_val;
    }

    // Uncacheable Controller
    @GetMapping(value = "/uncachePath/test.do")
    public String uncachePath() {
        String response_val = "uncachePath Called";
        logger.info("!!! cachePath has been called !!!");

        // This is temporary map value. You can test change this value as new one
        HashMap<String, String> testMap = new HashMap<String, String>();
        testMap.put("key2", "두번째값입니다.");

        demoService.getRestData("http://localhost:8080", "/demoApplication/listSample.do", testMap, HttpMethod.POST);
        return response_val;
    }
}