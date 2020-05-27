package com.hyd.elasticjobclient;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Job {

    @JSONField(serialize = false)
    private String key;

    private String jobName;

    private String jobType;

    private String jobClass;

    private String cron;

    private String jobParameter;

    private int shardingTotalCount = 1;

    private String shardingItemParameters;

    private boolean streamingProcess;

    private String description;

    private int instanceCount;
}
