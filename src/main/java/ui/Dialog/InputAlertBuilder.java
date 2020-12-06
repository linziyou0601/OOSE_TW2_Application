package ui.Dialog;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import mvvm.ViewManager;

import java.util.ArrayList;
import java.util.List;

public class InputAlertBuilder implements IAlertBuilder {
    JFXDialogLayout layout = new JFXDialogLayout();
    String title;
    String text;
    AlertButtonType alertButtonType;
    JFXTextField inputTextField = new JFXTextField();
    JFXButton defaultButton = new JFXButton("送出");
    JFXButton cancelButton = new JFXButton("取消");
    JFXAlert<String> alert = new JFXAlert<>(ViewManager.getPrimaryStage());

    public InputAlertBuilder(String title, String text, AlertButtonType alertButtonType) {
        this.title = title;
        this.text = text;
        this.alertButtonType = alertButtonType;
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
        // 設定label
        Label label = new Label(title);
        label.setFont(Font.font("Microsoft JhengHei", FontWeight.BOLD, 20));
        layout.setHeading(label);
        return this;
    }

    @Override
    public IAlertBuilder setBody() {
        inputTextField.setFont(Font.font("Microsoft JhengHei", FontWeight.NORMAL, 16));
        layout.setBody(new VBox(new Label(text), inputTextField));
        return this;
    }

    @Override
    public IAlertBuilder setDefaultButton(String text) {
        if(text!=null) defaultButton.setText(text);
        return this;
    }

    @Override
    public IAlertBuilder setCancelButton(String text) {
        if(text!=null) cancelButton.setText(text);
        return this;
    }

    @Override
    public IAlertBuilder setAlertStyle() {
        // 預設配置
        String defaultBack = "#2778c4";
        String cancelBack = "#757575";
        Insets buttonPadding = new Insets(8, 16, 8, 16);
        Font buttonFont = Font.font("Microsoft JhengHei", FontWeight.BOLD, 12);
        List<Node> buttons = new ArrayList<>();

        // 依alertButtonType調整按鈕
        switch(alertButtonType) {
            case OKCANCEL:
                buttons.add(defaultButton);
                buttons.add(cancelButton);
                break;
            case OK:
            default:
                buttons.add(defaultButton);
        }

        // 設定defaultButton
        defaultButton.setFont(buttonFont);
        defaultButton.setTextFill(Color.valueOf("#FFFFFF"));
        defaultButton.setStyle("-fx-background-color: " + defaultBack);
        defaultButton.setPadding(buttonPadding);
        defaultButton.setDefaultButton(true);
        defaultButton.setOnAction(addEvent -> {
            alert.setResult(inputTextField.textProperty().get());
            alert.hideWithAnimation();
        });

        // 設定cancelButton
        cancelButton.setFont(buttonFont);
        cancelButton.setTextFill(Color.valueOf("#FFFFFF"));
        cancelButton.setStyle("-fx-background-color: " + cancelBack);
        cancelButton.setPadding(buttonPadding);
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(addEvent -> {
            alert.setResult(null);
            alert.hideWithAnimation();
        });

        // 將Button設定到layout上
        layout.setActions(buttons);
        return this;
    }

    @Override
    public JFXAlert getAlert() {
        return alert;
    }
}
