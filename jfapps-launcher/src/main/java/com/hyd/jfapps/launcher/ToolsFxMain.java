package com.hyd.jfapps.launcher;

import com.hyd.jfapps.appbase.AppContext;
import com.hyd.jfapps.launcher.appmanager.AppManager;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToolsFxMain {

    public static void main(String[] args) throws Exception {
        AppManager.APP_CONTEXT.put(AppContext.APP_ICON, Icons.icon("/logo.png"));
        AppManager.init();

        Application.launch(ToolsFxApplication.class, args);
    }
}
