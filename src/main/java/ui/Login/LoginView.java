package ui.Login;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import mvvm.View;
import mvvm.ViewModelProviders;


public class LoginView implements View {

    @FXML
    private JFXTextField accountInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXButton signUpBtn;

    @FXML
    private JFXButton loginBtn;

    private LoginViewModel loginViewModel;

    @Override
    public void initialize() {

        loginViewModel = ViewModelProviders.getInstance().get(LoginViewModel.class);

        // 換頁鈕
        signUpBtn.setOnAction(e -> loginViewModel.signUp());
        loginBtn.setOnAction(e -> loginViewModel.login());

        // 雙向綁定View資料和ViewModel資料
        accountInput.textProperty().bindBidirectional(loginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(loginViewModel.passwordProperty());

        // 綁定 loginAlert 變數
        loginViewModel.loginAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());
    }
}