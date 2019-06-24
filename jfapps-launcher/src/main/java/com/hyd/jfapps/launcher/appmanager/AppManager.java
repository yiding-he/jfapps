package com.hyd.jfapps.launcher.appmanager;

import com.hyd.jfapps.appbase.GlobalContext;
import com.hyd.jfapps.launcher.AppLoadingException;
import com.hyd.jfapps.launcher.JarScanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppManager {

    public static final GlobalContext GLOBAL_CONTEXT = new GlobalContext();

    /**
     * App 实例容器对象列表
     */
    private static final List<AppContainer> APP_CONTAINERS = new ArrayList<>();

    /**
     * 加载 App 过程中产生的异常信息
     */
    private static final List<AppLoadingException> APP_LOADING_EXCEPTIONS = new ArrayList<>();

    public static List<AppContainer> getAppContainers() {
        return APP_CONTAINERS;
    }

    public static List<AppLoadingException> getAppLoadingExceptions() {
        return APP_LOADING_EXCEPTIONS;
    }

    public static void init() {
        List<File> appFiles = JarScanner.scanAppJars("apps/");
        for (File appFile : appFiles) {
            try {
                APP_CONTAINERS.add(new AppContainer(appFile));
            } catch (Throwable e) {
                log.error("Error loading app", e);
                APP_LOADING_EXCEPTIONS.add(new AppLoadingException(e));
            }
        }
    }

    public static void appClosed(AppContainer appContainer) {
        appContainer.closeApp();
    }
}
