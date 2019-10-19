package com.hyd.jfapps.zkclient.zk;

import lombok.Data;

@Data
public class ZkNode {

    private String name;

    private int childrenCount;
}
