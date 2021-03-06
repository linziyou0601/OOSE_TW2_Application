package ui.Dialog;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import mvvm.ViewManager;

public class LoadingAlertBuilder implements IAlertBuilder {
    JFXDialogLayout layout = new JFXDialogLayout();
    JFXAlert<String> alert = new JFXAlert<>(ViewManager.getPrimaryStage());

    public LoadingAlertBuilder() {
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContent(layout);
    }

    @Override
    public IAlertBuilder setOverlayClose(boolean value) {
        alert.setOverlayClose(value);
        return this;
    }

    @Override
    public IAlertBuilder setHeading() {
        Label label = new Label("載入中...");
        label.setFont(Font.font("Microsoft JhengHei", FontWeight.BOLD, 20));
        layout.setHeading(label);
        return this;
    }

    @Override
    public IAlertBuilder setBody() {
        layout.setBody(new JFXSpinner());
        return this;
    }

    @Override
    public IAlertBuilder setDefaultButton(String text) {
        return this;
    }

    @Override
    public IAlertBuilder setCancelButton(String text) {
        return this;
    }

    @Override
    public IAlertBuilder setAlertStyle() {
        return this;
    }

    @Override
    public JFXAlert getAlert() {
        return alert;
    }
}
