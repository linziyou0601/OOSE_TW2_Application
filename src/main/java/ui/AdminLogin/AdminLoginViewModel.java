package ui.AdminLogin;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.SessionContext;
import model.Admin;
import model.User;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import ui.AdminMain.AdminMainView;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Login.LoginView;
import ui.Main.MainView;
import ui.Register.RegisterView;

public class AdminLoginViewModel extends ViewModel {

    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<JFXAlert> loginAlert = new SimpleObjectProperty<>();
    private ObjectProperty<JFXAlert> loadingAlert = new SimpleObjectProperty<>();

    public AdminLoginViewModel(DBMgr dbmgr) {
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

    // 邏輯處理：前往使用者登入頁面
    public void toUserLogin() {
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：驗證登入資料
    public void login(){
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        showLoading();
        dbmgr.getAdminByAccount(account.get())
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    Admin admin;
                    @Override
                    public void onNext(Admin result) {
                        admin = result;
                    }
                    @Override
                    public void onComplete(){
                        stopLoading();
                        // 驗證登入資料並執行登入邏輯
                        if(!admin.validate(password.get())) {
                            triggerFailedAlert("密碼錯誤");
                        } else {
                            sessionContext.set("admin", admin);
                            clearInput();
                            ViewManager.navigateTo(AdminMainView.class);
                        }
                    }
                    @Override
                    public void onError(Throwable e){
                        stopLoading();
                        triggerFailedAlert("帳號錯誤");
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
