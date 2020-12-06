package ui.Register;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewModel;
import main.SessionContext;
import mvvm.ViewManager;
import model.User;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Login.LoginView;
import ui.Main.MainView;

import java.util.Optional;

public class RegisterViewModel extends ViewModel {

    private StringProperty username = new SimpleStringProperty();
    private StringProperty account = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty passwordConfirm = new SimpleStringProperty();
    private ObjectProperty<JFXAlert> registerAlert = new SimpleObjectProperty<>();
    private ObjectProperty<JFXAlert> loadingAlert = new SimpleObjectProperty<>();

    public RegisterViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty usernameProperty(){
        return username;
    }
    public StringProperty accountProperty(){
        return account;
    }
    public StringProperty emailProperty(){
        return email;
    }
    public StringProperty passwordProperty(){ return password; }
    public StringProperty passwordConfirmProperty(){
        return passwordConfirm;
    }
    public ObjectProperty<JFXAlert> registerAlertProperty(){
        return registerAlert;
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
        username.set("");
        account.set("");
        email.set("");
        password.set("");
        passwordConfirm.set("");
    }

    // 邏輯處理：前往登入頁面
    public void signIn() {
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：驗證註冊資料
    public void submit(){
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        showLoading();
        dbmgr.getUserByAccount(account.get())
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    User existUser;
                    @Override
                    public void onNext(User result) {
                        existUser = result;
                    }
                    @Override
                    public void onComplete(){
                        String prompt = null;
                        // 驗證註冊資料
                        if(existUser!=null) prompt = "帳號已被使用";
                        else if(account.get()==null || account.get().equals("")) prompt = "帳號未輸入";
                        else if(email.get()==null || email.get().equals("")) prompt = "電子信箱未輸入";
                        else if(password.get()==null || password.get().equals("")) prompt = "密碼未輸入";
                        else if(!password.get().equals(passwordConfirm.get())) prompt = "密碼不一致";
                        else if(username.get()==null || username.get().equals("")) prompt = "使用者名稱未輸入";

                        // 執行註冊邏輯
                        if(prompt!=null) {
                            stopLoading();
                            triggerFailedAlert(prompt);
                        } else {
                            String password_hash = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.get().toCharArray());
                            // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
                            dbmgr.insertUser(new User(account.get(), password_hash, username.get(), email.get()))
                                    .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                                    .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                                    .subscribe(new RxJavaCompletableObserver() {
                                        @Override
                                        public void onComplete() {
                                            stopLoading();
                                            clearInput();
                                            triggerSucceedAlert();
                                        }
                                    });
                            // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
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
        registerAlert.set(alert);
    }

    // 邏輯處理：觸發成功
    public void triggerSucceedAlert() {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.SUCCESS, "成功註冊", "導向登入畫面", IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<Boolean> result = alert.showAndWait();
        if(result.isPresent()){
            ViewManager.navigateTo(LoginView.class);
        }
    }
}
