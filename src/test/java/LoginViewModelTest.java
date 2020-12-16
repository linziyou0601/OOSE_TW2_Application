import at.favre.lib.crypto.bcrypt.BCrypt;
import com.sun.javafx.application.PlatformImpl;
import database.DBMgr;
import de.saxsys.javafx.test.JfxRunner;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.TestScheduler;
import model.User;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import ui.Login.LoginViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JfxRunner.class)
class LoginViewModelTest {

    Map<String, User> usersMock = new HashMap<>();
    DBMgr dbmgr;
    LoginViewModel loginViewModel;
    TestScheduler scheduler;

    @BeforeEach
    public void setup() {
        PlatformImpl.startup(()->{});           //初始化JavaFx的PlatformImpl
        dbmgr = Mockito.mock(DBMgr.class);
        scheduler = new TestScheduler();
        loginViewModel = new LoginViewModel(dbmgr);
        usersMock.put("account1", new User("account1", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username1", "accu1@testmail.com"));
        usersMock.put("account2", new User("account2", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username2", "accu2@testmail.com"));
        usersMock.put("account3", new User("account3", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username3", "accu3@testmail.com"));
        usersMock.put("account4", new User("account4", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username4", "accu4@testmail.com"));
        usersMock.put("account5", new User("account5", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username5", "accu5@testmail.com"));
    }

    @BeforeEach
    public void mockUpDBMgr() {
        Mockito.when(dbmgr.getUserByAccount(Mockito.anyString())).thenAnswer((Answer<Observable<User>>) invocation -> {
            String account = invocation.getArgument(0);
            Observable observable = Observable.create((ObservableOnSubscribe<User>) subscriber -> {
                subscriber.onNext(usersMock.get(account));
                subscriber.onComplete();
            });
            return observable;
        });
    }

    @ParameterizedTest
    @MethodSource("mockLoginUser")
    public void testLogin(String account, String password) {
        // Give
        loginViewModel.accountProperty().set(account);
        loginViewModel.passwordProperty().set(password);

        // When
        loginViewModel.loginValid();

        // Wait for Async Task at most 1 sec
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> loginViewModel.loginValidProperty().get()!=-1);

        // Then
        int loginValid = loginViewModel.loginValidProperty().get();
        assertEquals(1, loginValid);
    }

    static Stream<Arguments> mockLoginUser() {
        return Stream.of(
                Arguments.of("account1", "pwd123"),
                Arguments.of("account2", "pwd123"),
                Arguments.of("account3", "pwd123")
        );
    }
}