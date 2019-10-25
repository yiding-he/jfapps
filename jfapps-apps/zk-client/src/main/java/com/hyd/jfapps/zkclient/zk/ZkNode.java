package com.hyd.jfapps.zkclient.zk;

import lombok.Data;

@Data
public class ZkNode {

    private String name;

    private String fullName;

    private int childrenCount;
}
