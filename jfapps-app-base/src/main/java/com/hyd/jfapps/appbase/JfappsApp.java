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

    public void initialize() {

    }

    public abstract Parent getRoot() throws Exception;
}
