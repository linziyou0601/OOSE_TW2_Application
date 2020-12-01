package ui.Dialog;

import com.jfoenix.controls.JFXAlert;

public interface IAlertBuilder {
    enum AlertType {
        ERROR, INFORMATION, WARNING, SUCCESS
    }
    enum AlertButtonType {
        OK, OKCANCEL, NONE
    }
    IAlertBuilder setOverlayClose(boolean value);
    IAlertBuilder setHeading();
    IAlertBuilder setBody();
    IAlertBuilder setDefaultButton(String text);
    IAlertBuilder setCancelButton(String text);
    IAlertBuilder setAlertStyle();
    JFXAlert getAlert();
}
