package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.Login.LoginView;
import ui.Main.MainView;

import java.net.URL;
import java.util.HashMap;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        /* 將所有View放入Map中 */
        ViewManager.addView(LoginView.class);
        ViewManager.addView(MainView.class);
        /* 設定用啟始頁面的View並顯示畫面 */
        ViewManager.initStage(primaryStage, LoginView.class);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
