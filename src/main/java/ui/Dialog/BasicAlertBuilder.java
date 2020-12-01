package ui.Dialog;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import main.ViewManager;

import java.util.ArrayList;
import java.util.List;

public class BasicAlertBuilder implements IAlertBuilder {
    JFXDialogLayout layout = new JFXDialogLayout();
    ImageView icon = new ImageView();
    String title;
    String text;
    AlertType alertType;
    AlertButtonType alertButtonType;
    JFXButton defaultButton = new JFXButton("確定");
    JFXButton cancelButton = new JFXButton("取消");
    JFXAlert<Boolean> alert = new JFXAlert<>(ViewManager.getPrimaryStage());

    public BasicAlertBuilder(AlertType alertType, String title, String text, AlertButtonType alertButtonType) {
        this.title = title;
        this.text = text;
        this.alertType = alertType;
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
        // 設定icon
        icon.setFitWidth(25);
        icon.setFitHeight(25);
        // 設定label
        Label label = new Label(title);
        label.setFont(Font.font("Microsoft JhengHei", FontWeight.BOLD, 20));
        label.setPadding(new Insets(0,0,0,10));
        // 設定hbox
        HBox container = new HBox(icon, label);
        container.setAlignment(Pos.CENTER_LEFT);
        // 設定到layout上
        layout.setHeading(container);
        return this;
    }

    @Override
    public IAlertBuilder setBody() {
        // 設定label
        Label label = new Label(text);
        label.setFont(Font.font("Microsoft JhengHei", FontWeight.NORMAL, 16));
        layout.setBody(label);
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

        // 依alertType調整顏色
        switch(alertType) {
            case ERROR:
                defaultBack = "#dc3545";
                icon.setImage(new Image("/images/error_52px.png"));
                break;
            case WARNING:
                defaultBack = "#dc3545";
                icon.setImage(new Image("/images/warning_52px.png"));
                break;
            case INFORMATION:
                defaultBack = "#2778c4";
                icon.setImage(new Image("/images/info_52px.png"));
                break;
            case SUCCESS:
                defaultBack = "#28a745";
                icon.setImage(new Image("/images/success_52px.png"));
                break;
        }

        // 依alertButtonType調整按鈕
        switch(alertButtonType) {
            case OK:
                buttons.add(defaultButton);
                break;
            case OKCANCEL:
                buttons.add(defaultButton);
                buttons.add(cancelButton);
                break;
            default:
        }

        // 設定defaultButton
        defaultButton.setFont(buttonFont);
        defaultButton.setTextFill(Color.valueOf("#FFFFFF"));
        defaultButton.setStyle("-fx-background-color: " + defaultBack);
        defaultButton.setPadding(buttonPadding);
        defaultButton.setDefaultButton(true);
        defaultButton.setOnAction(addEvent -> {
            alert.setResult(true);
            alert.hideWithAnimation();
        });

        // 設定cancelButton
        cancelButton.setFont(buttonFont);
        cancelButton.setTextFill(Color.valueOf("#FFFFFF"));
        cancelButton.setStyle("-fx-background-color: " + cancelBack);
        cancelButton.setPadding(buttonPadding);
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(addEvent -> {
            alert.setResult(false);
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
