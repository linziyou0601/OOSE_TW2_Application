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
    private JFXTextField accountInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXButton loginBtn;

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void maximumApplication(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.maximizedProperty().get());
    }

    @FXML
    public void minimumApplication(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private RegisterViewModel registerViewModel;

    public void initialize() {
        registerViewModel = ViewModelProviders.getInstance().get(RegisterViewModel.class);

        // 換頁鈕
        loginBtn.setOnAction(e -> registerViewModel.login());

        // 雙向綁定 accountInput 和 account 資料變數
        accountInput.textProperty().bindBidirectional(registerViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(registerViewModel.passwordProperty());

        // 綁定 errorAlert 變數
        registerViewModel.errorAlertProperty().addListener((ChangeListener<Alert>) (observable, oldAlert, newAlert) -> newAlert.show());
    }
}