package com.hyd.jfappsdemo.app1;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class AppController {

    public Label lblMessage;

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            lblMessage.setText("你选择了 " + file.getAbsolutePath());
        }
    }
}
