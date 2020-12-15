package ui.Register;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import main.SessionContext;
import model.User;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
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
    private StringProperty registerPrompt = new SimpleStringProperty();
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public RegisterViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
        loadingAlert.set(false);
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
    public StringProperty registerPromptProperty(){
        return registerPrompt;
    }
    public void setRegisterPrompt(String prompt){
        registerPrompt.set(prompt);
    }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
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
    public void submitValid(){
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
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
                        // User有資料
                        loadingAlert.set(false);
                        registerPrompt.set("帳號已被使用");
                    }
                    @Override
                    public void onError(Throwable e){
                        // User沒資料
                        loadingAlert.set(false);
                        // 驗證註冊資料
                        String prompt = null;
                        if(account.get()==null || account.get().equals("")) prompt = "帳號未輸入";
                        else if(email.get()==null || email.get().equals("")) prompt = "電子信箱未輸入";
                        else if(password.get()==null || password.get().equals("")) prompt = "密碼未輸入";
                        else if(!password.get().equals(passwordConfirm.get())) prompt = "密碼不一致";
                        else if(username.get()==null || username.get().equals("")) prompt = "使用者名稱未輸入";

                        // 執行註冊邏輯
                        if(prompt!=null) {
                            registerPrompt.set(prompt);
                        } else {
                            String password_hash = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.get().toCharArray());
                            // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
                            dbmgr.insertUser(new User(account.get(), password_hash, username.get(), email.get()))
                                    .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                                    .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                                    .subscribe(new RxJavaCompletableObserver() {
                                        @Override
                                        public void onComplete() {
                                            registerPrompt.set("成功");
                                        }
                                    });
                            // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
                        }
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：執行註冊換頁邏輯
    public void doRegister() {
        clearInput();
        ViewManager.navigateTo(LoginView.class);
    }
}
