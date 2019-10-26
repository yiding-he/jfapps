package com.hyd.jfapps.zkclient.event;

import lombok.Data;

@Data
public class AddNodeRequest {

    private String parent;

    public AddNodeRequest() {
    }

    public AddNodeRequest(String parent) {
        this.parent = parent;
    }
}
