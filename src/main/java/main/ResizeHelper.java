package main;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

//created by Alexander Berg
public class ResizeHelper {

    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void addListener(Stage stage) {
        Scene scene = stage.getScene();
        Parent root = scene.getRoot();
        addResizeListener(stage, root);
        addDragListener(stage, root);
        addFunctionButtonListener(stage, root);
    }

    private static void addFunctionButtonListener(Stage stage, Parent root) {
        JFXButton closeStageBtn = (JFXButton) root.lookup("#closeStageBtn");
        JFXButton maximumBtn = (JFXButton) root.lookup("#maximumBtn");
        JFXButton minimumBtn = (JFXButton) root.lookup("#minimumBtn");
        if(closeStageBtn != null) {
            closeStageBtn.setOnAction(e -> stage.close());
        }
        if(maximumBtn != null) {
            maximumBtn.setOnAction(e -> {
                stage.setMaximized(!stage.maximizedProperty().get());
            });
        }
        if(minimumBtn != null) {
            minimumBtn.setOnAction(e -> {
                stage.setIconified(true);
            });
        }
    }

    private static void addDragListener(Stage stage, Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private static void addResizeListener(Stage stage, Parent root) {
        JFXButton resizeBtn = (JFXButton) root.lookup("#resizeBtn");
        if(resizeBtn!=null) {
            resizeBtn.setOnMouseDragged(event -> {
                double newX = event.getScreenX() - stage.getX() + 13;
                double newY = event.getScreenY() - stage.getY() + 10;
                if (newX % 5 == 0 || newY % 5 == 0) {
                    if (newX > 550) {
                        stage.setWidth(newX);
                    } else {
                        stage.setWidth(550);
                    }

                    if (newY > 200) {
                        stage.setHeight(newY);
                    } else {
                        stage.setHeight(200);
                    }
                }
            });
        }
    }
}