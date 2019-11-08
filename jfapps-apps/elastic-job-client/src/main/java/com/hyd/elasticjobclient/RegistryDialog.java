package com.hyd.elasticjobclient;

import com.hyd.fx.dialog.AbstractDialog;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

public class RegistryDialog extends AbstractDialog {

    @Override
    protected Node getDialogContent() {
        return new BorderPane();
    }

    @Override
    protected Collection<? extends ButtonType> getButtonTypes() {
        return Arrays.asList(ButtonType.OK, ButtonType.CANCEL);
    }
}
