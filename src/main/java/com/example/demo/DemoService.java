package com.example.demo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    // You can set URL for cache.
    @Cacheable(value = "myAwesomeCache", cacheManager = "contentCacheManager", condition = "#url.startsWith('/realApplication/')", key = "#url + '::' + #param")
    public Map getRestData(String host, String url, Map param, HttpMethod method) {
        return getRestDataWithSession(host, url, param, method, null);
    }

    public Map getRestDataWithSession(String host, String url, Map param, HttpMethod method, String sessionId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        if (sessionId != null) {
            headers.add(headers.COOKIE, "JSESSIONID=" + sessionId);
        }

        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));

        String fullUri = host + url;
        String jsonParam = null;

        if (method.equals(HttpMethod.GET) || method.equals(HttpMethod.DELETE)) {
            fullUri += "?" + ofFromDataStr(param, true);
        } else {
            headers.setContentType(MediaType.APPLICATION_JSON);
            try {
                jsonParam = mapToJsonString(param);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        HttpEntity request = new HttpEntity(jsonParam, headers);
        ResponseEntity<String> response = restTemplate.exchange(fullUri, method, request, String.class);

        Map resultMap = null;

        try {
            resultMap = jsonToMap(response.getBody());
        } catch (IOException e) {
            logger.error("!!! getRestData {} !!!", e.getMessage());
        }
        return resultMap;
    }

    private static String ofFromDataStr(Map<Object, Object> data, boolean needEncode) {
        if (data == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");

            if (needEncode) {
                builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
            } else {
                builder.append(entry.getValue().toString());
            }
        }
        return builder.toString();
    }

    private static TreeMap jsonToMap(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        TreeMap<String, ArrayList<TreeMap>> map = mapper.readValue(json, TreeMap.class);
        return map;
    }

    private static String mapToJsonString(Map param) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(param);
    }

    private static Map listToMap(List<TreeMap> list, String key, String value) {
        Map retMap = new TreeMap();
        for (Map map : list) {
            String kValue = (String) map.get(key);
            String vValue = (String) map.get(value);
            retMap.put(kValue, vValue);
        }
        return retMap;
    }
}
