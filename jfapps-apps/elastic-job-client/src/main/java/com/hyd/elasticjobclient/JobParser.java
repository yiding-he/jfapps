package com.hyd.elasticjobclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class JobParser {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    static {
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        YAML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Job parse(String configText) {
        if (configText.startsWith("{")) {
            return parseJson(configText);
        } else {
            return parseYaml(configText);
        }
    }

    private static Job parseYaml(String configText) {
        try {
            Job job = YAML_MAPPER.readValue(configText, Job.class);
            job.setJobFormat(JobFormat.YAML);
            return job;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Job parseJson(String configText) {
        try {
            Job job = JSON_MAPPER.readValue(configText, Job.class);
            job.setJobFormat(JobFormat.JSON);
            return job;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //////////////////////////////////////////////////////////////

    public static String modifyJob(
            String configText, JobFormat jobFormat,
            String jobName, String description, String cron, String jobParameter, int shardingTotalCount
    ) {
        try {
            JsonNode node;

            if (jobFormat == JobFormat.JSON) {
                node = JSON_MAPPER.readTree(configText);
            } else {
                node = YAML_MAPPER.readTree(configText);
            }

            if (node instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) node;
                objectNode.put("jobName", jobName);
                objectNode.put("description", description);
                objectNode.put("cron", cron);
                objectNode.put("jobParameter", jobParameter);
                objectNode.put("shardingTotalCount", shardingTotalCount);
            }

            if (jobFormat == JobFormat.JSON) {
                return JSON_MAPPER.writeValueAsString(node);
            } else {
                return YAML_MAPPER.writeValueAsString(node);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
