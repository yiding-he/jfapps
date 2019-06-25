package com.hyd.jfapps.launcher.appmanager;

import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.*;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.hyd.jfapps.launcher.appmanager.AppManager.GLOBAL_CONTEXT;

@Slf4j
public class AppContainer {

    private File appFile;

    private String appName;

    private Image icon;

    private String jfappsClassName;

    private AppInfo appInfo;

    private String configFilePath;

    //////////////////////////////////////////////////////////////

    private AppClassLoader appClassLoader;

    private JfappsApp appInstance;

    private Class<? extends JfappsApp> jfappsClass;

    //////////////////////////////////////////////////////////////

    public AppContainer(File appFile) throws Exception {
        this.appFile = appFile;
        init();
    }

    //////////////////////////////////////////////////////////////

    public JfappsApp getAppInstance() {
        return appInstance;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public AppClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public String getAppName() {
        return appName;
    }

    public Image getIcon() {
        return icon;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    //////////////////////////////////////////////////////////////

    /**
     * 读取 jar 文件，加载 app 相关元数据，但不运行 app
     */
    @SuppressWarnings("unchecked")
    private void init() throws Exception {

        log.info("Loading app {}", appFile.getName());
        appClassLoader = new AppClassLoader(AppContainer.class.getClassLoader(), appFile);

        //////////////////////////////////////////////////////////////

        Properties appProps = new Properties();

        try (ZipFile zipFile = new ZipFile(appFile)) {
            ZipEntry propertiesEntry = zipFile.getEntry("jfapps.properties");
            if (propertiesEntry != null) {
                appProps.load(zipFile.getInputStream(propertiesEntry));
            }

            ZipEntry logoEntry = zipFile.getEntry("logo.png");
            if (logoEntry != null) {
                icon = new Image(zipFile.getInputStream(logoEntry));
            } else {
                icon = Icons.icon("/logo.png");
            }
        }

        jfappsClassName = appProps.getProperty("app-main-class");

        if (jfappsClassName == null || jfappsClassName.trim().length() == 0) {
            throw new AppLoadingException("App name not found.");
        }

        //////////////////////////////////////////////////////////////

        jfappsClass = (Class<? extends JfappsApp>) appClassLoader.loadClass(jfappsClassName);
        appInfo = jfappsClass.getAnnotation(AppInfo.class);

        appName = Optional
            .ofNullable(appInfo)
            .map(AppInfo::name)
            .orElse(jfappsClass.getSimpleName());
    }

    public JfappsApp getOrCreateAppInstance() throws Exception {
        if (appInstance == null) {
            initApp();
        }
        return appInstance;
    }

    // 初始化 App 实例，准备 AppContext 对象，执行 initialize() 方法
    public void initApp() throws Exception {

        if (appFile == null) {
            throw new IllegalStateException("appFile is null");
        }

        if (jfappsClassName == null) {
            throw new IllegalStateException("jfappsClassName is null");
        }

        //////////////////////////////////////////////////////////////

        configFilePath = getConfigFileName(jfappsClassName);

        appInstance = jfappsClass.newInstance();
        appInstance.setGlobalContext(GLOBAL_CONTEXT);
        appInstance.setClassLoader(appClassLoader);

        AppContextImpl appContext = new AppContextImpl();
        appContext.setPrimaryStage(ToolsFxApplication.primaryStage);
        appContext.setHostServices(ToolsFxApplication.hostServices);
        appContext.setIcon(icon);
        appContext.setConfigFilePath(configFilePath);
        appContext.loadProperties();
        appInstance.setAppContext(appContext);

        if (appInstance.getOnInitialized() != null) {
            try {
                appInstance.getOnInitialized().run();
            } catch (Exception e) {
                AlertDialog.error("错误", e);
            }
        }
    }

    public void closeApp() {
        try {
            appInstance = null;
        } catch (Exception e) {
            AlertDialog.error("错误", e);
        }
    }

    private static String getConfigFileName(String appMainClassName) {
        return System.getProperty("user.home") + "/.jfapps/" + appMainClassName + "/app.properties";
    }
}
