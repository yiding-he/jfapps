package com.hyd.jfapps.zkclient.event;

import com.hyd.jfapps.zkclient.node.ZkNodePane;

public class ZkNodeSelectedEvent {

    private ZkNodePane zkNodePane;

    public ZkNodeSelectedEvent(ZkNodePane zkNodePane) {
        this.zkNodePane = zkNodePane;
    }

    public ZkNodePane getZkNodePane() {
        return zkNodePane;
    }
}
