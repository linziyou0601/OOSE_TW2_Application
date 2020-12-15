import mvvm.ViewModelProviders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ui.Booking.BookingViewModel;
import ui.Booking.BookingView;

class BookingTest{
    //List<Integer> list;
    private BookingViewModel bookingViewModel;

    @BeforeEach
    protected void setup() {
        //list = new ArrayList<>();
        //list = List.of(1, 10, 100, 1000, 1001, 1002);
        bookingViewModel = ViewModelProviders.getInstance().get(BookingViewModel.class);
    }

    @AfterEach
    protected void tearDown(){
        //list = null;
        if(bookingViewModel != null){ bookingViewModel = null; }
    }

    /*@Test
    public void testSize() {
        assertEquals(6, list.size());
    }*/

    /*@Test
    public void testIndex() {
        int testX = list.get(5);
        assertEquals(1002, testX);
    }*/

    /*@Test
    public void testExpectError() {
        //assertEquals(2147483646, Integer.sum(2147183646, 200000));               //這個可以看到test錯誤
        assertEquals(2147483646, Integer.sum(2147183646, 300000));  //這個可以看到test正確
    }*/

    /*@Test
    public void testExpectSuccess() {
        assertThrows(ArithmeticException.class, () -> Integer.divideUnsigned(42, 0));
    }*/

    @Test
    public void testViewModel() {
        if(ViewModelProviders.getInstance().equals(bookingViewModel)) {
            assertEquals(ViewModelProviders.getInstance(),bookingViewModel);
        } else {
            System.out.println(ViewModelProviders.getInstance()+ "\n" + bookingViewModel);
        }
    }

    @Test
    public void testEmpty() {
        bookingViewModel = null;
        assertTrue(bookingViewModel==null);
    }
}