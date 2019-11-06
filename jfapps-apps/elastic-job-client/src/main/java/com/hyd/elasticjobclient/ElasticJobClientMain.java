package com.hyd.elasticjobclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ElasticJobClientMain extends Application {

    public static void main(String[] args) {
        Application.launch(ElasticJobClientMain.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(new BorderPane(), 400, 300));
        primaryStage.show();
    }
}
