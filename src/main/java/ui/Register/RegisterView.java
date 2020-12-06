package ui.Register;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import mvvm.View;
import mvvm.ViewModelProviders;

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

    @Override
    public void initialize() {
        registerViewModel = ViewModelProviders.getInstance().get(RegisterViewModel.class);

        // 換頁鈕
        signInBtn.setOnAction(e -> registerViewModel.signIn());
        submitBtn.setOnAction(e -> registerViewModel.submit());

        // 雙向綁定View資料和ViewModel資料
        usernameInput.textProperty().bindBidirectional(registerViewModel.usernameProperty());
        accountInput.textProperty().bindBidirectional(registerViewModel.accountProperty());
        emailInput.textProperty().bindBidirectional(registerViewModel.emailProperty());
        passwordInput.textProperty().bindBidirectional(registerViewModel.passwordProperty());
        passwordConfirmInput.textProperty().bindBidirectional(registerViewModel.passwordConfirmProperty());

        // 綁定 registerAlert 變數
        registerViewModel.registerAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());
    }
}