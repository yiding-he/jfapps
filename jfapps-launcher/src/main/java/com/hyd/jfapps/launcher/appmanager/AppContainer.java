package com.hyd.jfapps.launcher.appmanager;

import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.AppClassLoader;
import javafx.scene.image.Image;
import lombok.Data;

import java.io.File;

@Data
public class AppContainer {

    private File appFile;

    private String appName;

    private Image icon;

    private AppClassLoader appClassLoader;

    private JfappsApp appInstance;


}
