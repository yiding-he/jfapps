package com.hyd.elasticjobclient;

import static com.hyd.elasticjobclient.Icons.icon;
import static com.hyd.fx.builders.ButtonBuilder.iconButton;

import com.hyd.fx.app.AppThread;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.jfapps.appbase.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.elasticjob.lite.reg.zookeeper.ZookeeperConfiguration;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

@AppInfo(
    name = "Elastic Job 客户端",
    author = "yiding-he",
    url = "https://github.com/yiding-he/jfapps",
    category = AppCategory.RPC
)
public class ElasticJobClientMain extends JfappsApp {

    private static Stage primaryStage;

    private BorderPane borderPane;

    private Label placeHolderLabel = new Label("");

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private ComboBox<String> addressesCombo;

    private TabPane registryTabs;

    private Button openRegButton;

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(ElasticJobClientMain.class);
    }

    @Override
    public Parent getRoot() throws Exception {
        primaryStage = appContext.getPrimaryStage();
        return root();
    }

    private BorderPane root() {
        borderPane = new BorderPane(
            placeHolderLabel,
            addressBar(), null, null, null
        );
        borderPane.setPrefSize(900, 600);
        return borderPane;
    }

    private Node addressBar() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_LEFT);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);

        hBox.getChildren().addAll(
            label(),
            addressesCombo = addresses(),
            openRegButton = iconButton("打开", icon(FontAwesomeIcon.DESKTOP), this::openRegistry),
            iconButton("清空", icon(FontAwesomeIcon.TRASH), this::clearAddresses)
        );
        return hBox;
    }

    private void clearAddresses() {

        if (!AlertDialog.confirmYesNo("清空历史", "是否要删除所有历史记录？")) {
            return;
        }

        addressesCombo.getItems().clear();
        addPredefinedAddress(addressesCombo);
        UserPreferences.save(ConfigKey.ServerAddresses, "");
    }

    private Label label() {
        Label label = new Label("注册中心地址（[alias]ip:port/name）：");
        label.setMinWidth(Region.USE_PREF_SIZE);
        return label;
    }

    private void openRegistry() {
        String address = addressesCombo.getValue();
        if (address == null || !address.matches("^\\[.+].+:\\d+/.+$")) {
            AlertDialog.error("地址格式不正确。");
            return;
        }

        String alias = StringUtils.substringBefore(address, "]") + "]";
        String host = StringUtils.substringAfter(StringUtils.substringBefore(address, "/"), "]");
        String regName = StringUtils.substringAfter(address, "/");

        BackgroundTask.runTask(() -> {
            ZookeeperConfiguration conf = new ZookeeperConfiguration(host, regName);
            ZookeeperRegistryCenter registryCenter = new ZookeeperRegistryCenter(conf);
            registryCenter.init();

            addAddress(addressesCombo.getItems(), address);

            List<String> addresses = UserPreferences.get(ConfigKey.ServerAddresses);
            if (!addresses.contains(address)) {
                UserPreferences.append(ConfigKey.ServerAddresses, address);
            }

            AppThread.runUIThread(() -> {

                if (registryTabs == null) {
                    registryTabs = new TabPane();
                    borderPane.setCenter(registryTabs);
                }

                Tab tab = new Tab(alias + regName);
                ElasticJobPane elasticJobPane = new ElasticJobPane(registryCenter, tab);
                tab.setContent(elasticJobPane);
                tab.setClosable(false);
                registryTabs.getTabs().add(tab);
                elasticJobPane.init();
                registryTabs.getSelectionModel().select(tab);
            });

        }).whenBeforeStart(() -> {
            placeHolderLabel.setText("第一次打开会比较慢，请耐心等待……");
            openRegButton.setDisable(true);
        }).whenTaskFinish(() -> {
            openRegButton.setDisable(false);
        }).whenTaskFail(e -> {
            AlertDialog.error("连接注册中心失败", e);
        }).start();
    }

    private ComboBox<String> addresses() {
        ComboBox<String> c = new ComboBox<>();
        c.setEditable(true);
        c.setMaxWidth(Double.POSITIVE_INFINITY);

        List<String> addresses = UserPreferences.get(ConfigKey.ServerAddresses);
        c.getItems().addAll(addresses);
        addPredefinedAddress(c);

        HBox.setHgrow(c, Priority.ALWAYS);
        return c;
    }

    private void addPredefinedAddress(ComboBox<String> c) {
        addAddress(c.getItems(), "[UAT]172.16.10.21:2181/frxsJob");
        addAddress(c.getItems(), "[UAT]172.16.10.21:2181/fundJob");
        addAddress(c.getItems(), "[UAT]172.16.10.21:2181/accountantJob");
        addAddress(c.getItems(), "[UAT]172.16.10.21:2181/fundClearingJob");
    }

    private void addAddress(ObservableList<String> list, String item) {
        if (!list.contains(item)) {
            list.add(item);
        }
    }
}
