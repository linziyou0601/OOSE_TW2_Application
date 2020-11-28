package ui.Login;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.MainApplication;
import main.ViewModelProviders;


public class LoginView {

    @FXML
    private TextField accountInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button loginBtn;

    private LoginViewModel loginViewModel;

    public void initialize() {
        loginViewModel = ViewModelProviders.getInstance().get(LoginViewModel.class);

        // 換頁鈕
        loginBtn.setOnAction(
                e -> loginViewModel.login()
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(Schedulers.io())                     //請求完成後在io執行緒中執行
                .doOnNext(s -> MainApplication.setAccount(s))   //在onNext之前進行前處理
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    @Override
                    public void onNext(String s) {}
                    @Override
                    public void onError(Throwable e) { new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).show(); }
                    @Override
                    public void onComplete() {
                        loginViewModel.clearInput();
                        MainApplication.showPage("Main");
                    }
                })
        );

        // 雙向綁定 accountInput 和 account 資料變數
        accountInput.textProperty().bindBidirectional(loginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(loginViewModel.passwordProperty());
    }
}