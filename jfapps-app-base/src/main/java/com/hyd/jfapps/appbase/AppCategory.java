package com.hyd.jfapps.appbase;

public enum AppCategory {

    RPC("远程接口调试"),
    REMOTE_SERVICE("远程服务"),
    DATABASE("数据库工具"),
    TEXT("文本处理"),
    IMAGE("图像处理"),
    SOUND_VIDEO("音频视频处理"),
    SYSTEM("系统工具"),
    LIFE("生活助手"),
    FINALCIAL("记账理财"),
    OTHER("其他"),

    ///////////////////////////////////////////////////////////////
    ;

    private String description;

    AppCategory(String description) {
        this.description = description;
    }
}
