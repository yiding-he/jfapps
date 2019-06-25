package com.hyd.jfapps.appbase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public abstract class JfappsApp {

    protected ClassLoader classLoader;

    protected GlobalContext globalContext;

    protected AppContext appContext;

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    public void setGlobalContext(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected FXMLLoader fxmlLoader(String fxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setClassLoader(classLoader);
        fxmlLoader.setLocation(getClass().getResource(fxmlPath));
        return fxmlLoader;
    }

    //////////////////////////////////////////////////////////////

    public String getAppName() {
        AppInfo appInfo = getClass().getAnnotation(AppInfo.class);
        if (appInfo == null) {
            return getClass().getSimpleName();
        } else {
            return appInfo.name();
        }
    }

    //////////////////////////////////////////////////////////////

    // 用于初始化数据，此时界面尚未展示，不能操作界面元素
    private Runnable onInitialized;

    // 用于初始化界面
    private Runnable onShown;

    // 当 App 要关闭时
    private Runnable onCloseRequest;

    public Runnable getOnInitialized() {
        return onInitialized;
    }

    public void setOnInitialized(Runnable onInitialized) {
        this.onInitialized = onInitialized;
    }

    public Runnable getOnShown() {
        return onShown;
    }

    public void setOnShown(Runnable onShown) {
        this.onShown = onShown;
    }

    public Runnable getOnCloseRequest() {
        return onCloseRequest;
    }

    public void setOnCloseRequest(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;
    }

    ///////////////////////////////////////////////////////////////

    public abstract Parent getRoot() throws Exception;
}
