package com.example.demo;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

import lombok.var;


@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class DemoWriteBackController {

    private final DemoWriteBackService demoWriteBackService;

    public DemoWriteBackController(DemoWriteBackService demoWriteBackService) {
        this.demoWriteBackService = demoWriteBackService;
    }

    private static final Logger logger = LoggerFactory.getLogger(DemoWriteBackController.class);;

    /*
     * Write-Back Caching pattern start
     */

    @PostMapping(value = "/cachePutPath", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cachePutPath(@RequestBody Map<Object, Object> requestBody){
        
        int paramId = Integer.parseInt(requestBody.get("Id").toString());
        String paramValue = requestBody.get("Value").toString();

        demoWriteBackService.create(paramId, paramValue);
        
        return new ResponseEntity<>("result successful", HttpStatus.OK);
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