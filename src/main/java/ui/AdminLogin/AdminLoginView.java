package ui.AdminLogin;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import mvvm.View;
import mvvm.ViewManager;
import mvvm.ViewModelProviders;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Main.MainView;


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

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        adminLoginViewModel = ViewModelProviders.getInstance().get(AdminLoginViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        accountInput.textProperty().bindBidirectional(adminLoginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(adminLoginViewModel.passwordProperty());

        // 綁定 Loading Alert 變數
        adminLoginViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 綁定 Login Valid 變數
        adminLoginViewModel.loginValidProperty().addListener((observable, oldValid, newValid) -> {
            switch((int)newValid){
                case 0:
                    showLoginFailedAlert();
                    break;
                case 1:
                    adminLoginViewModel.doLogin();
                    break;
            }
            adminLoginViewModel.setLoginValid(-1);
        });

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();
        });
    }

    //使用者登入鈕
    public void toUserLoginBtnClick() {
        adminLoginViewModel.toUserLogin();
    }

    //登入鈕
    public void loginBtnClick() {
        adminLoginViewModel.loginValid();
    }

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