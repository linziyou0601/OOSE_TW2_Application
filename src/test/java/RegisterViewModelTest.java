import at.favre.lib.crypto.bcrypt.BCrypt;
import com.sun.javafx.application.PlatformImpl;
import database.DBMgr;
import de.saxsys.javafx.test.JfxRunner;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import model.User;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import ui.Register.RegisterViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JfxRunner.class)
class RegisterViewModelTest {

    Map<String, User> usersMock = new HashMap<>();
    DBMgr dbmgr;
    RegisterViewModel registerViewModel;

    @BeforeEach
    public void setup() {
        PlatformImpl.startup(()->{});           //初始化JavaFx的PlatformImpl
        dbmgr = Mockito.mock(DBMgr.class);
        registerViewModel = new RegisterViewModel(dbmgr);
        usersMock.put("account1", new User("account1", BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray()), "username1", "accu1@testmail.com"));
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
        Mockito.when(dbmgr.insertUser(Mockito.any())).thenAnswer((Answer<Completable>) invocation -> {
            Completable completable = Completable.create(subscriber -> subscriber.onComplete());
            return completable;
        });
    }

    @ParameterizedTest
    @MethodSource("mockUserInputProvider")
    public void testRegister(String account, String username, String email, String password, String passwordConfirm, String result) {
        // Give
        registerViewModel.accountProperty().set(account);
        registerViewModel.usernameProperty().set(username);
        registerViewModel.emailProperty().set(email);
        registerViewModel.passwordProperty().set(password);
        registerViewModel.passwordConfirmProperty().set(passwordConfirm);

        // When
        registerViewModel.registerValid();

        // Wait for Async Task at most 1 sec
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> registerViewModel.getRegisterPrompt()!=null);

        // Then
        assertEquals(result, registerViewModel.getRegisterPrompt());
    }

    static Stream<Arguments> mockUserInputProvider() {
        return Stream.of(
                Arguments.of("account1", "username01", "abc@cde.com", "pwd123", "pwd123", "帳號已被使用"),
                Arguments.of(""        , "username01", "abc@cde.com", "pwd123", "pwd123", "帳號格式有誤"),
                Arguments.of("account2", "",           "abc@cde.com", "pwd123", "pwd123", "使用者名稱未輸入"),
                Arguments.of("account2", "username02", "abc",         "pwd123", "pwd123", "電子信箱格式有誤"),
                Arguments.of("account2", "username02", "",            "pwd123", "pwd123", "電子信箱格式有誤"),
                Arguments.of("account2", "username02", "abc@cde.com", "",       "pwd123", "密碼不一致或或未輸入"),
                Arguments.of("account2", "username02", "abc@cde.com", "pwd123", "pwd12",  "密碼不一致或或未輸入"),
                Arguments.of("account2", "username02", "abc@cde.com", "pwd123", "pwd123", "成功")
        );
    }
}