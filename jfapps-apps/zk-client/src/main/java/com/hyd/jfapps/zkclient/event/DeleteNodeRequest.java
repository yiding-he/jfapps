package com.hyd.jfapps.zkclient.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteNodeRequest {

    private String nodePath;
}
