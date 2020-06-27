package com.hyd.jfapps.zkclient.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZkConnectingEvent {

    private String address;
}
