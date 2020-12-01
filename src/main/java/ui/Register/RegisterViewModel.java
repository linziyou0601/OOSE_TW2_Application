package ui.Register;

import javafx.beans.property.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.SessionService;
import main.ViewManager;
import main.IViewModel;
import model.User;
import model.UserStorage;
import ui.Main.MainView;

public class RegisterViewModel implements IViewModel {

    private SessionService sessionService;
    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<Alert> errorAlert = new SimpleObjectProperty<>();

    private UserStorage userStorage = new UserStorage();

    public RegisterViewModel(SessionService sessionService) {
        this.sessionService = sessionService;
        userStorage.add(new User("A001", "pwd123"));
        userStorage.add(new User("A002", "pwd123"));
        userStorage.add(new User("A003", "pwd123"));
    }

    // =============== Getter及Setter ===============
    public StringProperty accountProperty(){
        return account;
    }
    public String getAccount() { return account.get(); }
    public StringProperty passwordProperty(){
        return password;
    }
    public String getPassword() { return password.get(); }
    public ObjectProperty errorAlertProperty() { return errorAlert; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：清除所有輸入資料
    public void clearInput() {
        account.set("");
        password.set("");
    }

    // 邏輯處理：驗證登入資料
    public void login(){
        User user = userStorage.find(account.get());
        if (user == null)
            errorAlert.set(new Alert(Alert.AlertType.ERROR, "帳號錯誤", ButtonType.OK));
        else if (!user.validate(password.get()))
            errorAlert.set(new Alert(Alert.AlertType.ERROR, "密碼錯誤", ButtonType.OK));
        else {
            sessionService.setUser(user);
            clearInput();
            ViewManager.navigateTo(MainView.class);
        }
    }
}
