package com.hyd.jfapps.launcher.appmanager;

import com.hyd.jfapps.appbase.AppContext;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.AppClassLoader;
import com.hyd.jfapps.launcher.AppLoadingException;
import com.hyd.jfapps.launcher.JarScanner;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class AppManager {

    public static final AppContext APP_CONTEXT = new AppContext();

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
        ClassLoader classLoader = AppManager.class.getClassLoader();

        for (File appFile : appFiles) {
            try {
                loadApp(classLoader, appFile);
            } catch (Throwable e) {
                log.error("Error loading app", e);
                APP_LOADING_EXCEPTIONS.add(new AppLoadingException(e));
            }
        }
    }

    private static void loadApp(ClassLoader classLoader, File appFile) throws Exception {
        AppContainer appContainer = new AppContainer();
        appContainer.setAppFile(appFile);
        appContainer.setAppClassLoader(new AppClassLoader(classLoader, appFile));

        Properties appProps = new Properties();

        try (ZipFile zipFile = new ZipFile(appFile)) {
            ZipEntry propertiesEntry = zipFile.getEntry("jfapps.properties");
            if (propertiesEntry != null) {
                appProps.load(zipFile.getInputStream(propertiesEntry));
            }

            ZipEntry logoEntry = zipFile.getEntry("logo.png");
            if (logoEntry != null) {
                appContainer.setIcon(new Image(zipFile.getInputStream(logoEntry)));
            }
        }

        ////////////////////////////////////////////////////////////

        String appMainClassName = appProps.getProperty("app-main-class");
        if (appMainClassName == null || appMainClassName.trim().length() == 0) {
            throw new AppLoadingException("App name not found.");
        }
        log.info("Loading app {}...", appMainClassName);

        AppClassLoader appClassLoader = appContainer.getAppClassLoader();
        Class<?> jfappsAppClass = appClassLoader.loadClass(appMainClassName);

        ////////////////////////////////////////////////////////////

        AppInfo appInfo = jfappsAppClass.getAnnotation(AppInfo.class);
        if (appInfo != null) {
            appContainer.setAppName(appInfo.name());
        } else {
            appContainer.setAppName(jfappsAppClass.getSimpleName());
        }

        ////////////////////////////////////////////////////////////

        JfappsApp jfappsApp = (JfappsApp) jfappsAppClass.newInstance();
        jfappsApp.setAppContext(APP_CONTEXT);
        jfappsApp.setClassLoader(appClassLoader);

        appContainer.setAppInstance(jfappsApp);
        APP_CONTAINERS.add(appContainer);
    }
}
