package ui.Main;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.MainApplication;
import main.ViewManager;
import main.ViewModelProviders;
import ui.Login.LoginView;

public class MainView {

    @FXML
    private Label promptLabel;

    @FXML
    private Button logoutBtn;

    private MainViewModel mainViewModel;

    public void initialize() {
        mainViewModel = ViewModelProviders.getInstance().get(MainViewModel.class);

        // 換頁鈕
        logoutBtn.setOnAction(e -> ViewManager.navigateTo(LoginView.class));

        // 將首頁資料綁定到mainViewModel的user資料
        promptLabel.textProperty().bindBidirectional(mainViewModel.accountProperty());
    }
}