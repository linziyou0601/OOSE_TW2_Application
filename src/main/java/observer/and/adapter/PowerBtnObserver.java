package observer.and.adapter;

import com.jfoenix.controls.JFXButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import devices.IoTDevice;

public class PowerBtnObserver implements Observer{
    JFXButton powerBtn;

    public PowerBtnObserver(JFXButton powerBtn){
        this.powerBtn = powerBtn;
    }

    @Override
    public void update(Observable device) {
        String powerIconPathPostfix, powerBtnColor;
        ImageView powerBtnIcon = (ImageView) powerBtn.getGraphic().lookup("#powerBtnIcon");
        if (((IoTDevice)device).getState().equals("ON")){
            powerIconPathPostfix = "red";
            powerBtnColor = "#FF0062"; //red
        } else {
            powerIconPathPostfix = "green";
            powerBtnColor = "#009688"; //green
        }
        powerBtnIcon.setImage(new Image("/images/power_" + powerIconPathPostfix + ".png"));
        powerBtn.setStyle("-fx-border-color: " + powerBtnColor + "; -fx-border-radius: 50%; -fx-background-radius: 50%;");
    }
}
