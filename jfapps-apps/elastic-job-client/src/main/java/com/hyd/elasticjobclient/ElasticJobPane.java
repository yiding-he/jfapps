package com.hyd.elasticjobclient;

import com.google.gson.Gson;
import com.hyd.fx.builders.ButtonBuilder;
import com.hyd.fx.builders.MenuBuilder;
import io.elasticjob.lite.lifecycle.api.JobOperateAPI;
import io.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class ElasticJobPane extends VBox {

    private String regName;

    private ZookeeperRegistryCenter registryCenter;

    private JobOperateAPI jobOperateAPI;

    private TableView<Job> tableView;

    private Tab parentTab;

    private ContextMenu contextMenu;

    @SuppressWarnings("unchecked")
    public ElasticJobPane(String regName, ZookeeperRegistryCenter registryCenter, Tab parentTab) {
        this.setPadding(new Insets(10));
        this.regName = regName;
        this.registryCenter = registryCenter;
        this.jobOperateAPI = new JobOperateAPIImpl(registryCenter);
        this.parentTab = parentTab;
        this.contextMenu = MenuBuilder.contextMenu(
            MenuBuilder.menuItem("修改...", () -> {}),
            MenuBuilder.menuItem("触发", () -> {})
        );

        //////////////////////////////////////////////////////////////

        this.tableView = new TableView<>();
        this.tableView.setRowFactory(tv -> {
            TableRow<Job> row = new TableRow<>();
            row.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                TableRow _row = (TableRow) event.getSource();
                if (_row.isEmpty() || event.getButton() != MouseButton.SECONDARY) {
                    return;
                }

                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }

                contextMenu.show(_row, event.getScreenX(), event.getScreenY());
            });
            return row;
        });

        TableColumn<Job, String> jobNameCol = new TableColumn<>("任务名称");
        jobNameCol.setCellValueFactory(new PropertyValueFactory<>("jobName"));

        TableColumn<Job, String> jobDescCol = new TableColumn<>("任务描述");
        jobDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Job, String> jobTypeCol = new TableColumn<>("任务类名");
        jobTypeCol.setCellValueFactory(new PropertyValueFactory<>("jobClass"));

        TableColumn<Job, String> jobCronCol = new TableColumn<>("执行计划");
        jobCronCol.setCellValueFactory(new PropertyValueFactory<>("cron"));

        TableColumn<Job, String> jobInstCol = new TableColumn<>("实例数");
        jobInstCol.setCellValueFactory(new PropertyValueFactory<>("instanceCount"));

        this.tableView.getColumns().addAll(
            jobNameCol, jobDescCol, jobTypeCol, jobCronCol, jobInstCol
        );

        this.getChildren().add(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        //////////////////////////////////////////////////////////////

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        buttons.setSpacing(10);
        buttons.getChildren().add(ButtonBuilder.button("关闭", () -> {
            this.registryCenter.close();
            this.parentTab.getTabPane().getTabs().remove(this.parentTab);
        }));

        this.getChildren().add(buttons);
    }

    public void init() {
        Gson gson = new Gson();

        List<Job> jobs = registryCenter.getChildrenKeys("/").stream()
            .map(key -> {
                String config = registryCenter.get("/" + key + "/config");
                Job job = gson.fromJson(config, Job.class);
                int numChildren = registryCenter.getNumChildren("/" + key + "/instances");
                job.setInstanceCount(numChildren);
                return job;
            })
            .collect(Collectors.toList());

        this.tableView.getItems().addAll(jobs);
    }
}
