package observer.and.adapter;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import devices.IoTDevice;

public class DeviceIconObserver implements IObserver {
    ImageView deviceImage;
    Label deviceNameLabel;

    public DeviceIconObserver(ImageView deviceImage, Label deviceNameLabel){
        this.deviceImage = deviceImage;
        this.deviceNameLabel = deviceNameLabel;
    }

    @Override
    public void update(IObservable device) {
        String deviceImagePathPostfix, deviceNameColor;
        if (((IoTDevice)device).getState().equals("ON")){
            deviceImagePathPostfix = "green";
            deviceNameColor = "#009688"; //green
        } else {
            deviceImagePathPostfix = "gray";
            deviceNameColor = "#A5A5A5"; //gray
        }
        deviceImage.setImage(new Image("/images/IoT_" + ((IoTDevice)device).getType() + "_" + deviceImagePathPostfix + ".png"));
        deviceNameLabel.setText(((IoTDevice)device).getName());
        deviceNameLabel.setTextFill(Color.valueOf(deviceNameColor));
    }
}

