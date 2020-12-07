package ui.AdminLogin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import mvvm.View;
import mvvm.ViewModelProviders;


public class AdminLoginView implements View {

    @FXML
    private JFXTextField accountInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXButton toUserLoginBtn;

    @FXML
    private JFXButton loginBtn;

    private AdminLoginViewModel adminLoginViewModel;

    @Override
    public void initialize() {

        adminLoginViewModel = ViewModelProviders.getInstance().get(AdminLoginViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        accountInput.textProperty().bindBidirectional(adminLoginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(adminLoginViewModel.passwordProperty());

        // 綁定 loginAlert 變數
        adminLoginViewModel.loginAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());

        // 綁定 Loading Alert 變數
        adminLoginViewModel.loadingAlertProperty().addListener((observable, oldAlert, newAlert) -> Platform.runLater(() -> newAlert.show()));
    }

    //使用者登入鈕
    public void toUserLoginBtnClick() {
        adminLoginViewModel.toUserLogin();
    }

    //登入鈕
    public void loginBtnClick() {
        adminLoginViewModel.login();
    }
}