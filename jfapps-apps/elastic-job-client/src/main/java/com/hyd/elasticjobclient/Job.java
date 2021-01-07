package com.hyd.elasticjobclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Job {

    @JsonIgnore
    private String key;

    @JsonIgnore
    private JobFormat jobFormat;

    private String jobName;

    private String jobType;

    private String jobClass;

    private String cron;

    private String jobParameter;

    private int shardingTotalCount = 1;

    private String shardingItemParameters;

    private boolean streamingProcess;

    private boolean misfire;

    private boolean failover;

    private boolean monitorExecution;

    private String description;

    private int instanceCount;
}
