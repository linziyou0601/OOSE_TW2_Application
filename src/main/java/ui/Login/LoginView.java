package ui.Login;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import mvvm.View;
import mvvm.ViewModelProviders;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
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

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        loginViewModel = ViewModelProviders.getInstance().get(LoginViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        accountInput.textProperty().bindBidirectional(loginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(loginViewModel.passwordProperty());

        // 綁定 Loading Alert 變數
        loginViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 綁定 Login Valid 變數
        loginViewModel.loginValidProperty().addListener((observable, oldValid, newValid) -> {
            switch((int)newValid){
                case 0:
                    showLoginFailedAlert();
                    break;
                case 1:
                    loginViewModel.doLogin();
                    break;
            }
            loginViewModel.setLoginValid(-1);
        });

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();
        });
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
    public void loginBtnClick() { loginViewModel.loginValid(); }

    //顯示 Loading Alert
    public void showLoading() {
        loadingAlert.show();
    }

    //停止 Loading Alert
    public void stopLoading() {
        loadingAlert.close();
    }

    //顯示失敗 Alert
    public void showLoginFailedAlert(){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "錯誤", "帳號或密碼錯誤", IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        alert.show();
    }
}