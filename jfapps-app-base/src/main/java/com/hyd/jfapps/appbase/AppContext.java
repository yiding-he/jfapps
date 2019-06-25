package com.hyd.jfapps.appbase;

import javafx.application.HostServices;
import javafx.scene.image.Image;

/**
 * App 用来获取信息或执行操作
 */
public interface AppContext {

    /**
     * 获取 App 自己的图标，如果没有则返回框架图标
     */
    Image getIcon();

    /**
     * 保存 App 自己的配置
     *
     * @param configName  配置名
     * @param configValue 配置值
     */
    void setConfiguration(String configName, String configValue);

    /**
     * 读取 App 的配置
     *
     * @param configName 配置名
     *
     * @return 配置值
     */
    String getConfiguration(String configName);


    /**
     * 获得 HostServices 对象
     */
    HostServices getHostServices();
}
