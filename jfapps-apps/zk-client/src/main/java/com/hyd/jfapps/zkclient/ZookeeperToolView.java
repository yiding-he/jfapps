package com.hyd.jfapps.zkclient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: ZookeeperToolView
 * @Description: Zookeeper工具
 * @author: xufeng
 * @date: 2019/4/8 17:00
 */

@Getter
@Setter
public abstract class ZookeeperToolView implements Initializable {

    @FXML
    protected SplitPane mainPane;

    @FXML
    protected Label lblStatus;

    @FXML
    protected ComboBox<Integer> cmbConnTimeout;

    @FXML
    protected TextField zkServersTextField;

    @FXML
    protected Button connectButton;

    @FXML
    protected Button refreshButton;

    @FXML
    protected TreeView<String> nodeTreeView;

    @FXML
    protected TextArea nodeDataValueTextArea;

    @FXML
    protected TextField A_VERSIONTextField;

    @FXML
    protected TextField C_TIMETextField;

    @FXML
    protected TextField C_VERSIONTextField;

    @FXML
    protected TextField CZXIDTextField;

    @FXML
    protected TextField DATA_LENGTHTextField;

    @FXML
    protected TextField EPHEMERAL_OWNERTextField;

    @FXML
    protected TextField M_TIMETextField;

    @FXML
    protected TextField MZXIDTextField;

    @FXML
    protected TextField NUM_CHILDRENTextField;

    @FXML
    protected TextField PZXIDTextField;

    @FXML
    protected TextField VERSIONTextField;

    @FXML
    protected TextField aclSchemeTextField;

    @FXML
    protected TextField aclIdTextField;

    @FXML
    protected TextField aclPermissionsTextField;

}