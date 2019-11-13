package com.hyd.elasticjobclient;

import lombok.Data;

@Data
public class Job {

    private String jobName;

    private String jobType;

    private String jobClass;

    private String cron;

    private String shardingTotalCount;

    private String shardingItemParameters;

    private boolean streamingProcess;

    private String description;

    private int instanceCount;
}
