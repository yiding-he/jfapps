package com.hyd.jfapps.zkclient.dialog;

import com.hyd.fx.dialog.form.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class NewNodeDialog extends FormDialog {

    private final TextFormField nodeName = new TextFormField("节点名称", "");

    private final CheckBoxFormField persistent = new CheckBoxFormField("持久节点");

    private final CheckBoxFormField sequential = new CheckBoxFormField("顺序节点");

    public String getNodeName() {
        return nodeName.getText();
    }

    public boolean isPersistent() {
        return persistent.isSelected();
    }

    public boolean isSequential() {
        return sequential.isSelected();
    }

    public NewNodeDialog(Stage owner) {
        super(owner);
        setTitle("新建节点");
        addField(nodeName);
        addField(persistent);
        addField(sequential);
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {
        closeOK();
    }
}
