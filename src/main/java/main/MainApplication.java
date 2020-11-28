package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;

public class MainApplication extends Application {

    public static HashMap<String, URL> pagePaneMap = new HashMap<>();
    public static Scene scene;
    public static Stage stage;
    public static String account;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        /* 將所有View放入Map中 */
        pagePaneMap.put("Login", getClass().getResource("/ui/LoginView.fxml"));
        pagePaneMap.put("Main", getClass().getResource("/ui/MainView.fxml"));
        /* 設定用啟始頁面的View */
        scene = new Scene(FXMLLoader.load(pagePaneMap.get("Login")));
        /* 顯示畫面 */
        stage = primaryStage;
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void showPage(String page){
        try {
            Parent root = FXMLLoader.load(pagePaneMap.get(page));
            stage.setTitle(page);
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAccount(String acc){
        account = acc;
    }

    public static String getAccount(){
        return account;
    }
}
