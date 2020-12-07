package ui.Login;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import mvvm.View;
import mvvm.ViewModelProviders;
import ui.Dialog.AlertDirector;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;


public class LoginView implements View {

    @FXML
    private JFXTextField accountInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXButton signUpBtn;

    @FXML
    private JFXButton loginBtn;

    @FXML
    private JFXButton toAdminLoginBtn;

    private LoginViewModel loginViewModel;

    @Override
    public void initialize() {

        loginViewModel = ViewModelProviders.getInstance().get(LoginViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        accountInput.textProperty().bindBidirectional(loginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(loginViewModel.passwordProperty());

        // 綁定 loginAlert 變數
        loginViewModel.loginAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());

        // 綁定 Loading Alert 變數
        loginViewModel.loadingAlertProperty().addListener((observable, oldAlert, newAlert) -> Platform.runLater(() -> newAlert.show()));
    }

    //管理員登入鈕
    public void toAdminLoginBtnClick() {
        loginViewModel.toAdminLogin();
    }

    //註冊鈕
    public void signUpBtnClick() {
        loginViewModel.signUp();
    }

    //登入鈕
    public void loginBtnClick() {
        loginViewModel.login();
    }
}