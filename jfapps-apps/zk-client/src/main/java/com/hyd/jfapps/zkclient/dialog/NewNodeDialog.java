package com.hyd.jfapps.zkclient.dialog;

import com.hyd.fx.dialog.form.FormDialog;
import com.hyd.fx.dialog.form.TextFormField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class NewNodeDialog extends FormDialog {

    private final TextFormField nodeName = new TextFormField("节点名称", "");

    public String getNodeName() {
        return nodeName.getText();
    }

    public NewNodeDialog(Stage owner) {
        super(owner);
        setTitle("新建节点");
        addField(nodeName);
    }

    @Override
    protected void okButtonClicked(ActionEvent event) {
        closeOK();
    }
}
