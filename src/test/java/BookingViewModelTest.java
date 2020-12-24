import com.sun.javafx.application.PlatformImpl;
import database.DBMgr;
import database.MySQLDBMgrImplProxy;
import de.saxsys.javafx.test.JfxRunner;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import model.Booking;
import model.Classroom;
import model.User;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import ui.Booking.BookingViewModel;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JfxRunner.class)
class BookingViewModelTest {

    MySQLDBMgrImplProxy mySQLDBMgrProxy;
    DBMgr dbmgr;
    BookingViewModel bookingViewModel;

    User currentUser, otherUser;
    Classroom selectedClassroom;
    List<Boolean> availableTime;

    @BeforeEach
    public void setup() {
        //-------------------- 初始化使用到的DBMgr和ViewModel --------------------
        PlatformImpl.startup(()->{});                                   // 初始化JavaFx的PlatformImpl
        mySQLDBMgrProxy = new MySQLDBMgrImplProxy();                    // 實例化DBMgr Impl
        dbmgr = new DBMgr(mySQLDBMgrProxy);                             // 實例化DBMgr
        bookingViewModel = new BookingViewModel(dbmgr);                 // 實例化BookingViewModel

        //-------------------- 初始化使用到的變數（測試資料的一部分） --------------------
        currentUser = mySQLDBMgrProxy.getUserByAccount("GodJackTest");  // 取得測試使用者
        otherUser = mySQLDBMgrProxy.getUserByAccount("OtherUserTest");  // 取得另一位測試使用者
        selectedClassroom = mySQLDBMgrProxy.getClassroomById("MA214");  // 取得測試用教室

        //-------------------- 新增測試用資料（booking）到資料庫 --------------------
        mySQLDBMgrProxy.insertBooking(new Booking("2030-12-31", 3, 6, selectedClassroom, currentUser));
        mySQLDBMgrProxy.insertBooking(new Booking("2030-12-31", 10, 12, selectedClassroom, otherUser));
        // ↑ 測試資料：該使用者已借用MA214，借用時間：1990年12月31日 03:00 ~ 07:00
        // ↑ 測試資料：另一名使用者已借用MA214，借用時間：1990年12月31日 10:00 ~ 13:00

        availableTime = mySQLDBMgrProxy.getAvailableTime(selectedClassroom.getId(), "2030-12-31");  // 拿必要資料（availableTime）
    }

    @AfterEach
    public void postTestData() {
        List<Booking> currentUserBookings = mySQLDBMgrProxy.getBookingsByAccount(currentUser.getAccount());
        for(Booking booking: currentUserBookings){
            mySQLDBMgrProxy.deleteBookingById(booking.getId());
        }
        List<Booking> otherUserBookings = mySQLDBMgrProxy.getBookingsByAccount(otherUser.getAccount());
        for(Booking booking: otherUserBookings){
            mySQLDBMgrProxy.deleteBookingById(booking.getId());
        }
    }

    @ParameterizedTest
    @MethodSource("testDataForEachBooking")
    public void testBooking(String selectedDate, String startTime, String endTime, String expectedResult) {
        // 給定
        try {
            //-------------------- 將ViewModel的private變數暫時改為可用（方便測試） --------------------
            Field currentUserField = BookingViewModel.class.getDeclaredField("currentUser");
            Field selectedDateField = BookingViewModel.class.getDeclaredField("selectedDate");
            Field selectedClassroomField = BookingViewModel.class.getDeclaredField("selectedClassroom");
            currentUserField.setAccessible(true);
            selectedDateField.setAccessible(true);
            selectedClassroomField.setAccessible(true);
            //-------------------- 給定測試資料 --------------------
            currentUserField.set(bookingViewModel, currentUser);
            selectedDateField.set(bookingViewModel, selectedDate);
            selectedClassroomField.set(bookingViewModel, selectedClassroom);
            bookingViewModel.availableTimeProperty().setAll(availableTime);
            bookingViewModel.timeStartProperty().set(startTime);
            bookingViewModel.timeEndProperty().set(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 當執行
        bookingViewModel.submit();

        // 等待BookingViewModel的異步存取資料庫（最多等待 1 秒）
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> bookingViewModel.bookingPromptProperty().get()!=null);

        // 斷言結果
        String actualResult = bookingViewModel.bookingPromptProperty().get();
        assertEquals(expectedResult, actualResult);
    }

    static Stream<Arguments> testDataForEachBooking() {
        return Stream.of(
                Arguments.of("2030-12-31", "1", "3", "成功"),                 // 時間分離
                Arguments.of("2030-12-31", "2", "4", "該時段已借用其他教室"),   // 時間相交
                Arguments.of("2030-12-31", "3", "5", "該時段已借用其他教室"),   // 時間重疊
                Arguments.of("2030-12-31", "4", "6", "該時段已借用其他教室"),   // 時間重疊
                Arguments.of("2030-12-31", "5", "7", "該時段已借用其他教室"),   // 時間重疊
                Arguments.of("2030-12-31", "6", "8", "該時段已借用其他教室"),   // 時間相交
                Arguments.of("2030-12-31", "7", "9", "成功"),                 // 時間分離
                Arguments.of("2030-12-31", "3", "7", "該時段已借用其他教室"),   // 時間相等
                Arguments.of("2030-12-31", "2", "8", "該時段已借用其他教室")    // 時間重疊
        );
    }

    // Mock一個DBMgr，強制讓資料庫出錯，回傳Null值給bookingViewModel，才能測PATH 5
    public void forceException() {
        dbmgr = Mockito.mock(DBMgr.class);
        Mockito.when(dbmgr.getDuplicateBooking(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer((Answer<Observable<Boolean>>) invocation -> {
            Observable observable = Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
                subscriber.onNext(null);
                subscriber.onComplete();
            });
            return observable;
        });
        bookingViewModel = new BookingViewModel(dbmgr);
    }

    @ParameterizedTest
    @MethodSource("testDataForEachPaths")
    public void testWhiteBoxBasisPaths(boolean forcedError, String selectedDate, String startTime, String endTime, String expectedResult) {
        // 給定
        try {
            if(forcedError){ forceException(); }
            //-------------------- 將ViewModel的private變數暫時改為可用（方便測試） --------------------
            Field currentUserField = BookingViewModel.class.getDeclaredField("currentUser");
            Field selectedDateField = BookingViewModel.class.getDeclaredField("selectedDate");
            Field selectedClassroomField = BookingViewModel.class.getDeclaredField("selectedClassroom");
            currentUserField.setAccessible(true);
            selectedDateField.setAccessible(true);
            selectedClassroomField.setAccessible(true);
            //-------------------- 給定測試資料 --------------------
            currentUserField.set(bookingViewModel, currentUser);
            selectedDateField.set(bookingViewModel, selectedDate);
            selectedClassroomField.set(bookingViewModel, selectedClassroom);
            bookingViewModel.availableTimeProperty().setAll(availableTime);
            bookingViewModel.timeStartProperty().set(startTime);
            bookingViewModel.timeEndProperty().set(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 當執行
        bookingViewModel.submit();

        // 等待BookingViewModel的異步存取資料庫（最多等待 1 秒）
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> bookingViewModel.bookingPromptProperty().get()!=null);

        // 斷言結果
        String actualResult = bookingViewModel.bookingPromptProperty().get();
        assertEquals(expectedResult, actualResult);
    }

    static Stream<Arguments> testDataForEachPaths() {
        return Stream.of(
                //PATH 1: Ending time of booking is earlier than starting time.
                Arguments.of(false, "2030-12-31", "3", "3", "結束時間必需大於開始時間"),
                //PATH 2: The user has booked other classrooms in the same period.
                Arguments.of(false, "2030-12-31", "2", "4", "該時段已借用其他教室"),
                //PATH 3: The classroom is unavailable in the period.
                Arguments.of(false, "2030-12-31", "10", "12", "時間已被借用"),
                //PATH 4: The classroom is unavailable in the period.
                Arguments.of(false, "2030-12-31", "7", "9", "成功"),
                //PATH 5: Available time of the classroom is unavailable from the Database.
                Arguments.of(true, "2030-12-31", "5", "7", "無法查詢教室")
        );
    }
}