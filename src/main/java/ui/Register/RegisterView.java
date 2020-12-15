package ui.Register;

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
import ui.Login.LoginView;

import java.util.Optional;

public class RegisterView implements View {

    @FXML
    private JFXTextField usernameInput;

    @FXML
    private JFXTextField accountInput;

    @FXML
    private JFXTextField emailInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXPasswordField passwordConfirmInput;

    @FXML
    private JFXButton signInBtn;

    @FXML
    private JFXButton submitBtn;

    private RegisterViewModel registerViewModel;

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        registerViewModel = ViewModelProviders.getInstance().get(RegisterViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        usernameInput.textProperty().bindBidirectional(registerViewModel.usernameProperty());
        accountInput.textProperty().bindBidirectional(registerViewModel.accountProperty());
        emailInput.textProperty().bindBidirectional(registerViewModel.emailProperty());
        passwordInput.textProperty().bindBidirectional(registerViewModel.passwordProperty());
        passwordConfirmInput.textProperty().bindBidirectional(registerViewModel.passwordConfirmProperty());

        // 綁定 Loading Alert 變數
        registerViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 綁定 Register Prompt 變數
        registerViewModel.registerPromptProperty().addListener((observable, oldPrompt, newPrompt) -> {
            switch(newPrompt){
                case "":
                    break;
                case "成功":
                    showRegisterSucceedAlert();
                    break;
                default:
                    showRegisterFailedAlert(newPrompt);
                    break;
            }
            registerViewModel.setRegisterPrompt("");
        });

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();
        });
    }

    //登入鈕
    public void signInBtnClick() {
        registerViewModel.signIn();
    }

    //送出鈕
    public void submitBtnClick() {
        registerViewModel.submitValid();
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
    public void showRegisterFailedAlert(String prompt){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "錯誤", prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        alert.show();
    }

    //顯示成功 Alert
    public void showRegisterSucceedAlert() {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.SUCCESS, "成功註冊", "導向登入畫面", IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<Boolean> result = alert.showAndWait();
        if(result.isPresent()){
            registerViewModel.doRegister();
        }
    }
}