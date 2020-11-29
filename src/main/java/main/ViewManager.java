package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;

public class ViewManager {
    public static HashMap<String, URL> viewPaneMap = new HashMap<>();
    public static Scene scene;
    public static Stage stage;

    /* 將View放入Map中 */
    public static void addView(Class viewClass) {
        String viewName = viewClass.getSimpleName();
        viewPaneMap.put(viewName, MainApplication.class.getResource("/ui/"+viewName+".fxml"));
    }

    public static void initStage(Stage primaryStage, Class viewClass) throws Exception{
        String viewName = viewClass.getSimpleName();
        scene = new Scene(FXMLLoader.load(viewPaneMap.get(viewName)));
        stage = primaryStage;
        stage.setScene(scene);
    }

    public static void navigateTo(Class viewClass){
        if(viewClass != null) {
            String viewName = viewClass.getSimpleName();
            try {
                Parent root = FXMLLoader.load(viewPaneMap.get(viewName));
                stage.setTitle(viewName);
                scene.setRoot(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
