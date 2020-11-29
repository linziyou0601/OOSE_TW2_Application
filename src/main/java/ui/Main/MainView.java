package ui.Main;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.MainApplication;
import main.ViewManager;
import main.ViewModelProviders;
import ui.Login.LoginView;

public class MainView {

    @FXML
    private Label accountLabel;

    @FXML
    private JFXButton logoutBtn;

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

    private MainViewModel mainViewModel;

    public void initialize() {
        mainViewModel = ViewModelProviders.getInstance().get(MainViewModel.class);

        // 換頁鈕
        logoutBtn.setOnAction(e -> ViewManager.navigateTo(LoginView.class));

        // 將首頁資料綁定到mainViewModel的user資料
        accountLabel.textProperty().bindBidirectional(mainViewModel.accountProperty());
    }
}