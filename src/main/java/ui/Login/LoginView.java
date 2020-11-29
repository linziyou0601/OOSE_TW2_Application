package ui.Login;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.MainApplication;
import main.ViewManager;
import main.ViewModelProviders;
import ui.Main.MainView;


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
        loginBtn.setOnAction(e -> loginViewModel.login());

        // 雙向綁定 accountInput 和 account 資料變數
        accountInput.textProperty().bindBidirectional(loginViewModel.accountProperty());
        passwordInput.textProperty().bindBidirectional(loginViewModel.passwordProperty());

        // 綁定 errorAlert 變數
        loginViewModel.errorAlertProperty().addListener((ChangeListener<Alert>) (observable, oldAlert, newAlert) -> newAlert.show());
    }
}