package com.hyd.jfapps.launcher.appmanager;

import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.AppClassLoader;
import lombok.Data;

@Data
public class AppContainer {

    private AppClassLoader appClassLoader;

    private JfappsApp appInstance;


}
