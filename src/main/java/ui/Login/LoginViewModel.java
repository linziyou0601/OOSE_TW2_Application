package ui.Login;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import main.SessionContext;
import model.Classroom;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import model.User;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Main.MainView;
import ui.Register.RegisterView;

import java.util.List;

public class LoginViewModel extends ViewModel {

    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<JFXAlert> loginAlert = new SimpleObjectProperty<>();
    private ObjectProperty<JFXAlert> loadingAlert = new SimpleObjectProperty<>();

    public LoginViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
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
    public ObjectProperty<JFXAlert> loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：設定 loading Alert()
    public void showLoading() {
        IAlertBuilder alertBuilder = new LoadingAlertBuilder();
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        loadingAlert.set(alert);
    }

    // 邏輯處理：停止 loading Alert()
    public void stopLoading() {
        loadingAlert.get().close();
    }

    // 邏輯處理：清除所有輸入資料
    public void clearInput() {
        account.set("");
        password.set("");
    }

    // 邏輯處理：前往註冊頁面
    public void signUp() {
        ViewManager.navigateTo(RegisterView.class);
    }

    // 邏輯處理：驗證登入資料
    public void login(){
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        showLoading();
        dbmgr.getUserByAccount(account.get())
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    User user;
                    @Override
                    public void onNext(User result) {
                        user = result;
                    }
                    @Override
                    public void onComplete(){
                        stopLoading();
                        String prompt = null;
                        // 驗證登入資料
                        if(user == null) prompt = "帳號錯誤";
                        else if(!user.validate(password.get())) prompt = "密碼錯誤";
                        // 執行登入邏輯
                        if(prompt!=null) {
                            triggerFailedAlert(prompt);
                        } else {
                            sessionContext.set("user", user);
                            clearInput();
                            ViewManager.navigateTo(MainView.class);
                        }
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：觸發失敗
    public void triggerFailedAlert(String prompt){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "錯誤", prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        loginAlert.set(alert);
    }
}
