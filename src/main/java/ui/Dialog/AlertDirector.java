package ui.Dialog;

public class AlertDirector {
    IAlertBuilder alertBuilder;

    public AlertDirector(IAlertBuilder alertBuilder) {
        this.alertBuilder = alertBuilder;
    }
    public void build() {
        alertBuilder.setOverlayClose(false)
                    .setHeading()
                    .setBody()
                    .setAlertStyle();
    }
}
