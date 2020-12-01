package ui.Login;

import com.jfoenix.controls.JFXAlert;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.SessionService;
import main.ViewManager;
import main.IViewModel;
import model.User;
import model.UserStorage;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Main.MainView;

public class LoginViewModel implements IViewModel {

    private SessionService sessionService;
    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<JFXAlert> loginAlert = new SimpleObjectProperty<>();

    private UserStorage userStorage = new UserStorage();

    public LoginViewModel(SessionService sessionService) {
        this.sessionService = sessionService;
        userStorage.add(new User("A001", "pwd123"));
        userStorage.add(new User("A002", "pwd123"));
        userStorage.add(new User("A003", "pwd123"));
    }

    // =============== Getter及Setter ===============
    public StringProperty accountProperty(){
        return account;
    }
    public StringProperty passwordProperty(){
        return password;
    }
    public ObjectProperty<JFXAlert> loginAlertProperty(){
        return loginAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：清除所有輸入資料
    public void clearInput() {
        account.set("");
        password.set("");
    }

    // 邏輯處理：驗證登入資料
    public void login(){
        User user = userStorage.find(account.get());
        String prompt = null;

        if (user == null) {
            prompt = "帳號錯誤";
        } else if (!user.validate(password.get())) {
            prompt = "密碼錯誤";
        } else {
            sessionService.setUser(user);
            clearInput();
            ViewManager.navigateTo(MainView.class);
        }

        if(prompt!=null) {
            // Builder Pattern：建立BasicAlert
            IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "錯誤", "帳號錯誤", IAlertBuilder.AlertButtonType.OKCANCEL);
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            JFXAlert alert = alertBuilder.getAlert();
            loginAlert.set(alert);
        }
    }
}
