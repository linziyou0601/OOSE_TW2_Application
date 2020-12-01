package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.HashMap;

public class ViewManager {
    private static HashMap<String, URL> viewPaneMap = new HashMap<>();
    private static Scene scene;
    private static Stage stage;

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
                scene = new Scene(root);
                stage.setTitle(viewName);
                stage.setScene(scene);
                stage.setWidth(((Pane)root).getMinWidth());
                stage.setHeight(((Pane)root).getMinHeight());
                centerStage(stage, root);
                ResizeHelper.addListener(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void centerStage(Stage s, Parent root) {
        double width = ((Region)root).getPrefWidth();
        double height = ((Region)root).getPrefHeight();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        s.setX((screenBounds.getWidth() - width) / 2);
        s.setY((screenBounds.getHeight() - height) / 2);
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
