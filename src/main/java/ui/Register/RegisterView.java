package ui.Register;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.ViewModelProviders;

public class RegisterView {

    @FXML
    private JFXTextField usernameInput;

    @FXML
    private JFXTextField accountInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXPasswordField passwordConfirmInput;

    @FXML
    private JFXButton signInBtn;

    @FXML
    private JFXButton submitBtn;

    private RegisterViewModel registerViewModel;

    public void initialize() {
        registerViewModel = ViewModelProviders.getInstance().get(RegisterViewModel.class);

        // 換頁鈕
        signInBtn.setOnAction(e -> registerViewModel.signIn());
        submitBtn.setOnAction(e -> registerViewModel.submit());

        // 雙向綁定 accountInput 和 account 資料變數
        usernameInput.textProperty().bindBidirectional(registerViewModel.usernameProperty());
        accountInput.textProperty().bindBidirectional(registerViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(registerViewModel.passwordProperty());
        passwordConfirmInput.textProperty().bindBidirectional(registerViewModel.passwordConfirmProperty());

        // 綁定 registerAlert 變數
        registerViewModel.registerAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());
    }
}