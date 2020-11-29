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

    private static double xOffset = 0;
    private static double yOffset = 0;

    /* 將View放入Map中 */
    public static void addView(Class viewClass) {
        String viewName = viewClass.getSimpleName();
        viewPaneMap.put(viewName, MainApplication.class.getResource("/ui/"+viewName+".fxml"));
    }

    public static void initStage(Stage primaryStage, Class viewClass) {
        stage = primaryStage;
        navigateTo(viewClass);
    }

    public static void navigateTo(Class viewClass) {
        if(viewClass != null) {
            String viewName = viewClass.getSimpleName();
            try {
                Parent root = FXMLLoader.load(viewPaneMap.get(viewName));
                root.setOnMousePressed(event -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                root.setOnMouseDragged(event -> {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                });
                scene = new Scene(root);
                stage.setTitle(viewName);
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
