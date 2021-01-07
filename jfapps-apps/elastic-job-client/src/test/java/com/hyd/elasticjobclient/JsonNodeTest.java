package com.hyd.elasticjobclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonNodeTest {

    public static void main(String[] args) throws Exception {
        String s = "{\"name\":\"111111\"}";
        JsonMapper jsonMapper = new JsonMapper();
        JsonNode jsonNode = jsonMapper.readTree(s);
        System.out.println(jsonNode.getClass());
    }
}
