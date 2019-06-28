package com.hyd.jfapps.launcher;

import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.appmanager.AppContainer;
import com.hyd.jfapps.launcher.appmanager.AppManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToolsFxApplication extends Application {

    public static HostServices hostServices;

    public static Stage primaryStage;

    private TabPane tabPane;

    @Override
    public void start(Stage primaryStage) {
        ToolsFxApplication.hostServices = getHostServices();
        ToolsFxApplication.primaryStage = primaryStage;

        Icons.setStageIcon(primaryStage);

        Parent root = createRoot();
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = Math.max(visualBounds.getWidth() - 200, 1000);
        double height = Math.max(visualBounds.getHeight() - 200, 600);

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add("/main.css");

        primaryStage.setTitle("小工具集合");
        primaryStage.setScene(scene);
        primaryStage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - width) / 2);
        primaryStage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - height) / 2);

        primaryStage.setOnShown(event -> {
            AppManager.init();
            tabPane.getTabs().add(mainTab(tabPane));
            root.requestFocus();
        });

        primaryStage.show();
        System.out.println("__OK__");
    }

    private Parent createRoot() {
        return mainTabPane();
    }

    private HBox searchBox() {
        TextField textField = new TextField();
        textField.setPromptText("搜索");
        textField.setPrefWidth(300);
        textField.textProperty().addListener((_ob, _old, _new) -> {
            search(_new);
        });
        return new HBox(textField);
    }

    private void search(String keyword) {
        appMappings.forEach((appContainer, parent) -> {

            boolean match = keyword.trim().length() == 0 ||
                appContainer.getAppName().toLowerCase().contains(keyword.toLowerCase());

            parent.setVisible(match);
        });
    }

    private TabPane mainTabPane() {
        tabPane = new TabPane();
        return tabPane;
    }

    private Map<AppContainer, Parent> appMappings = new HashMap<>();

    private Tab mainTab(TabPane tabPane) {

        FlowPane flowPane = new FlowPane();
        flowPane.setStyle("-fx-border-style: none");
        flowPane.setPadding(new Insets(0, 0, 15, 0));  // 以便容纳阴影
        flowPane.setHgap(30);
        flowPane.setVgap(30);

        List<AppContainer> containers = AppManager.getAppContainers();
        for (AppContainer container : containers) {
            Parent appPane = createAppPane(tabPane, container);
            appPane.managedProperty().bind(appPane.visibleProperty());
            appMappings.put(container, appPane);
            flowPane.getChildren().add(appPane);
        }

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setStyle("-fx-background-color:transparent");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox vBox = new VBox(25,
            searchBox(),
            scrollPane
        );
        vBox.setPadding(new Insets(20, 40, 40, 40));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setGraphic(Icons.iconView("/logo.png", 16));
        tab.setContent(vBox);
        return tab;
    }

    private Parent createAppPane(TabPane tabPane, AppContainer container) {
        VBox vBox = new VBox(5,
            new Label(container.getAppName()),
            createAppButton(tabPane, container)
        );
        vBox.setPadding(new Insets(5, 0, 0, 0));

        HBox appPane = new HBox(10,
            Icons.iconView(container.getIcon(), 64),
            vBox
        );
        appPane.getStyleClass().add("app-card");
        return appPane;
    }

    private SplitMenuButton createAppButton(TabPane tabPane, AppContainer container) {
        SplitMenuButton button = new SplitMenuButton();
        button.setText("打开");
        button.setUserData(container);
        button.setOnAction(e -> {
            SplitMenuButton b = (SplitMenuButton) e.getSource();
            chooseOrCreateTab(tabPane, container, b);
        });

        if (container.getAppInfo().author().length() > 0) {
            MenuItem menuItem = new MenuItem();
            menuItem.setText("作者：" + container.getAppInfo().author());
            menuItem.setOnAction(event -> {
                String url = container.getAppInfo().url();
                hostServices.showDocument(url);
            });
            button.getItems().add(menuItem);
        }

        return button;
    }

    private void chooseOrCreateTab(TabPane tabPane, AppContainer container, Control b) {
        for (Tab tab : tabPane.getTabs()) {
            if (Objects.equals(b.getUserData(), tab.getUserData())) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        Tab appTab = createAppTab(container);
        if (appTab != null) {
            tabPane.getTabs().add(appTab);
            tabPane.getSelectionModel().select(appTab);

            if (container.getAppInstance().getOnShown() != null) {
                container.getAppInstance().getOnShown().run();
            }
        }
    }

    private Tab createAppTab(AppContainer appContainer) {
        Parent root;

        try {
            JfappsApp app = appContainer.getOrCreateAppInstance();
            root = app.getRoot();
        } catch (Exception e) {
            log.error("", e);
            new Alert(Alert.AlertType.ERROR, e.toString(), ButtonType.OK).showAndWait();
            return null;
        }

        Tab tab = new Tab();
        tab.setUserData(appContainer);
        tab.setGraphic(Icons.iconView(appContainer.getIcon(), 16));
        tab.setText(appContainer.getAppName());
        tab.setContent(root);

        tab.setOnCloseRequest(event -> {
            Tab _tab = (Tab) event.getSource();
            AppContainer _appContainer = (AppContainer) _tab.getUserData();
            JfappsApp appInstance = _appContainer.getAppInstance();
            if (appInstance != null && appInstance.getOnCloseRequest() != null) {
                try {
                    appInstance.getOnCloseRequest().run();
                } catch (Exception e) {
                    AlertDialog.error("错误", e);
                }
            }
        });

        tab.setOnClosed(event -> {
            Tab _tab = (Tab) event.getSource();
            AppContainer _appContainer = (AppContainer) _tab.getUserData();
            AppManager.appClosed(_appContainer);
        });

        return tab;
    }

}
