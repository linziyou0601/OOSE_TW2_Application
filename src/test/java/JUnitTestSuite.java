import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses( { BookingViewModelTest.class, RegisterViewModelTest.class } )
public class JUnitTestSuite {
}

