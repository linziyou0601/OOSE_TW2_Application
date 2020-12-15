import at.favre.lib.crypto.bcrypt.BCrypt;
import database.DBMgr;
import database.MySQLDBMgrImplProxy;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import model.User;
import mvvm.RxJavaObserver;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import ui.Login.LoginViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginViewModelTest {

    Map<String, User> usersMock = new HashMap<>();
    DBMgr dbmgr;
    LoginViewModel loginViewModel;
    TestScheduler scheduler;

    @BeforeEach
    public void setup() {
        dbmgr = Mockito.mock(DBMgr.class);
        scheduler = new TestScheduler();
        loginViewModel = new LoginViewModel(dbmgr);
    }

    @BeforeEach
    public void mokeUsers() {
        usersMock.put("account1", new User("account1", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username1", "accu1@testmail.com"));
        usersMock.put("account2", new User("account2", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username2", "accu2@testmail.com"));
        usersMock.put("account3", new User("account3", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username3", "accu3@testmail.com"));
        usersMock.put("account4", new User("account4", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username4", "accu4@testmail.com"));
        usersMock.put("account5", new User("account5", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username5", "accu5@testmail.com"));
    }

    @ParameterizedTest
    @CsvSource({"account1,pwd123", "account2,pwd123", "account3,pwd123"})
    public void testLogin(String account, String password) {
        Mockito.when(dbmgr.syncGetUserByAccount(Mockito.anyString())).thenAnswer((Answer<User>) invocation -> {
            return usersMock.get(account);
        });

        loginViewModel.accountProperty().set(account);
        loginViewModel.passwordProperty().set(password);
        loginViewModel.loginValid();

        //Awaitility.await().timeout(2, TimeUnit.SECONDS).until(() -> loginViewModel.loginValidProperty().get()!=-1); //異步等待用

        int loginValid = loginViewModel.loginValidProperty().get();
        assertEquals(1, loginValid);
    }
}