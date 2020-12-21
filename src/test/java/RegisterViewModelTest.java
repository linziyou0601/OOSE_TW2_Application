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
import org.junit.jupiter.api.Test;
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
        //-------------------- 初始化使用到的DBMgr和ViewModel --------------------
        PlatformImpl.startup(()->{});                       // 初始化JavaFx的PlatformImpl
        dbmgr = Mockito.mock(DBMgr.class);                  // 實例化Mock版的DBMgr（因為測試內容與資料庫無關）
        registerViewModel = new RegisterViewModel(dbmgr);   // 實例化RegisterViewModel

        //-------------------- 新增測試用資料（user）到資料庫 --------------------
        String passwordHash = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, "pwd123".toCharArray());
        usersMock.put("testLeoAndAlice", new User("testLeoAndAlice", passwordHash, "weAreOOTA", "weAreOOTA@testmail.com"));
    }

    @BeforeEach
    public void mockUpDBMgr() {
        //將registerViewModel.registerValid()會用到的dbmgr方法mock起來
        Mockito.when(dbmgr.getUserByAccount(Mockito.anyString())).thenAnswer((Answer<Observable<User>>) invocation -> {
            String account = invocation.getArgument(0);
            Observable observable = Observable.create((ObservableOnSubscribe<User>) subscriber -> {
                subscriber.onNext(usersMock.get(account));
                subscriber.onComplete();
            });
            return observable;
        });
        //將registerViewModel.registerValid()會用到的dbmgr方法mock起來
        Mockito.when(dbmgr.insertUser(Mockito.any())).thenAnswer((Answer<Completable>) invocation -> {
            Completable completable = Completable.create(subscriber -> subscriber.onComplete());
            return completable;
        });
    }

    @ParameterizedTest
    @MethodSource("testDataForEachUser")
    public void testRegister(String account, String username, String email, String password, String passwordConfirm, String result) {
        // 給定
        registerViewModel.accountProperty().set(account);
        registerViewModel.usernameProperty().set(username);
        registerViewModel.emailProperty().set(email);
        registerViewModel.passwordProperty().set(password);
        registerViewModel.passwordConfirmProperty().set(passwordConfirm);

        // 當執行
        registerViewModel.registerValid();

        // 等待RegisterViewModel的異步存取資料庫（最多等待 1 秒）
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> registerViewModel.getRegisterPrompt()!=null);

        // 斷言結果
        assertEquals(result, registerViewModel.getRegisterPrompt());
    }

    static Stream<Arguments> testDataForEachUser() {
        return Stream.of(
                Arguments.of("testLeoAndAlice", "username01", "abc@cde.com", "pwd123", "pwd123", "帳號已被使用"),
                Arguments.of(""               , "username01", "abc@cde.com", "pwd123", "pwd123", "帳號格式有誤"),
                Arguments.of("testAccount"    , "",           "abc@cde.com", "pwd123", "pwd123", "使用者名稱未輸入"),
                Arguments.of("testAccount"    , "username02", "abc",         "pwd123", "pwd123", "電子信箱格式有誤"),
                Arguments.of("testAccount"    , "username02", "",            "pwd123", "pwd123", "電子信箱格式有誤"),
                Arguments.of("testAccount"    , "username02", "abc@cde.com", "",       "pwd123", "密碼不一致或或未輸入"),
                Arguments.of("testAccount"    , "username02", "abc@cde.com", "pwd123", "pwd12",  "密碼不一致或或未輸入"),
                Arguments.of("testAccount"    , "username02", "abc@cde.com", "pwd123", "pwd123", "成功")
        );
    }

    @Test
    public void testIsAccountValid() {
        // 給定
        String accountNull  = null;
        String accountEmpty = "";
        String accountAny   = "B10723000";

        // 當執行
        boolean validNullAccount  = registerViewModel.isAccountValid(accountNull);
        boolean validEmptyAccount = registerViewModel.isAccountValid(accountEmpty);
        boolean validAnyAccount   = registerViewModel.isAccountValid(accountAny);

        // 斷言結果
        assertEquals(false, validNullAccount);
        assertEquals(false, validEmptyAccount);
        assertEquals(true, validAnyAccount);
    }

    @Test
    public void testIsUsernameValid() {
        // 給定
        String usernameNull  = null;
        String usernameEmpty = "";
        String usernameAny   = "Where's Joe";

        // 當執行
        boolean validNullUsername  = registerViewModel.isAccountValid(usernameNull);
        boolean validEmptyUsername = registerViewModel.isAccountValid(usernameEmpty);
        boolean validAnyUsername   = registerViewModel.isAccountValid(usernameAny);

        // 斷言結果
        assertEquals(false, validNullUsername);
        assertEquals(false, validEmptyUsername);
        assertEquals(true, validAnyUsername);
    }

    @Test
    public void testIsEmailValid() {
        // 給定
        String emailNull     = null;
        String emailEmpty    = "";
        String emailInvalid1 = "AnyTyping";
        String emailInvalid2 = "test   space";
        String emailInvalid3 = "a123456@@testmail.com";
        String emailInvalid4 = "a123456@testmai";
        String emailValid    = "a123456@testmai.com";

        // 當執行
        boolean nullEmail     = registerViewModel.isEmailValid(emailNull);
        boolean emptyEmail    = registerViewModel.isEmailValid(emailEmpty);
        boolean invalidEmail1 = registerViewModel.isEmailValid(emailInvalid1);
        boolean invalidEmail2 = registerViewModel.isEmailValid(emailInvalid2);
        boolean invalidEmail3 = registerViewModel.isEmailValid(emailInvalid3);
        boolean invalidEmail4 = registerViewModel.isEmailValid(emailInvalid4);
        boolean validEmail    = registerViewModel.isEmailValid(emailValid);

        // 斷言結果
        assertEquals(false, nullEmail);
        assertEquals(false, emptyEmail);
        assertEquals(false, invalidEmail1);
        assertEquals(false, invalidEmail2);
        assertEquals(false, invalidEmail3);
        assertEquals(false, invalidEmail4);
        assertEquals(true, validEmail);
    }

    @Test
    public void testIsPasswordValid() {
        // 給定
        String passwordNull      = null;
        String passwordNullConf  = "123";
        String passwordEmpty     = "";
        String passwordEmptyConf = "123";
        String passwordAny       = "asdf123";
        String passwordAnyConf   = "aas123";
        String passwordValid     = "asdfgh123";
        String passwordValidConf = "asdfgh123";

        // 當執行
        boolean validNullPassword  = registerViewModel.isPasswordValid(passwordNull, passwordNullConf);
        boolean validEmptyPassword  = registerViewModel.isPasswordValid(passwordEmpty, passwordEmptyConf);
        boolean validAnyPassword  = registerViewModel.isPasswordValid(passwordAny, passwordAnyConf);
        boolean validValidPassword  = registerViewModel.isPasswordValid(passwordValid, passwordValidConf);

        // 斷言結果
        assertEquals(false, validNullPassword);
        assertEquals(false, validEmptyPassword);
        assertEquals(false, validAnyPassword);
        assertEquals(true, validValidPassword);
    }
}


