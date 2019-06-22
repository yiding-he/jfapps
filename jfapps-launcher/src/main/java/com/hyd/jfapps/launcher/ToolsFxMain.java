package com.hyd.jfapps.launcher;

import com.hyd.jfapps.launcher.appmanager.AppManager;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Enumeration;

@Slf4j
public class ToolsFxMain {

    public static void main(String[] args) throws Exception {
        AppManager.init();
        Application.launch(ToolsFxApplication.class, args);
    }
}
