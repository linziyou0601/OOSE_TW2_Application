package ui.Login;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.ViewModelProviders;


public class LoginView {

    @FXML
    private JFXTextField accountInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXButton signUpBtn;

    @FXML
    private JFXButton loginBtn;

    @FXML
    private JFXButton resizeBtn;

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

    private LoginViewModel loginViewModel;

    public void initialize() {

        loginViewModel = ViewModelProviders.getInstance().get(LoginViewModel.class);

        // 換頁鈕
        signUpBtn.setOnAction(e -> loginViewModel.signUp());
        loginBtn.setOnAction(e -> loginViewModel.login());

        // 雙向綁定 accountInput 和 account 資料變數
        accountInput.textProperty().bindBidirectional(loginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(loginViewModel.passwordProperty());

        // 綁定 loginAlert 變數
        loginViewModel.loginAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());
    }
}