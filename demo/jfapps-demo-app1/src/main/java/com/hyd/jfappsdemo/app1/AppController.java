package com.hyd.jfappsdemo.app1;

import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class AppController {

    public Label lblMessage;

    public void initialize() {

        // 你可以使用 jfapps 提供的配置存取接口，如果你不打算自己实现的话
        String lastChooseFile = MainApp.APP_CONTEXT.getConfiguration("lastChooseFile");

        if (lastChooseFile != null) {
            lblMessage.setText("你上次选择了 " + lastChooseFile);
        }
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            String path = file.getAbsolutePath();
            lblMessage.setText("你选择了 " + path);
            MainApp.APP_CONTEXT.setConfiguration("lastChooseFile", path);
        }
    }
}
