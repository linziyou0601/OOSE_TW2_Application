package ui.Login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.MainApplication;
import main.ViewManager;
import main.ViewModelProviders;
import ui.Main.MainView;


public class LoginView {

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