package com.fullteaching.e2e.no_elastest.functional.test.llmexperimentation;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("e2e")
@DisplayName("E2E tests for FullTeaching Login Session")
class DUserAccessCalendarTest extends BaseLoggedTest {
    protected final String courseName = "Pseudoscientific course for treating the evil eye";
    protected final String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

    public DUserAccessCalendarTest() {
        super();
    }

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }

    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "openvidumock", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @AccessMode(resID = "course", concurrency = 15, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    void teachercourseMainTest(String mail, String password, String role) {
        this.slowLogin(user, mail, password);
        try {

            NavigationUtilities.toCoursesHome(driver); //4lines
            Wait.notTooMuch(driver).until(ExpectedConditions.presenceOfElementLocated(By.xpath(FIRST_COURSE_XPATH)));
            Click.element(driver, By.xpath(FIRST_COURSE_XPATH));
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
        } catch (Exception e) {
            fail("Failed to load Courses Tabs" + e.getClass() + ": " + e.getLocalizedMessage());
        }
        //Check tabs
        //Home tab
        try {
            CourseNavigationUtilities.go2Tab(driver, HOME_ICON); //4lines
        } catch (Exception e) {
            fail("Failed to load home tab" + e.getClass() + ": " + e.getLocalizedMessage());
        }
        try {
            CourseNavigationUtilities.go2Tab(driver, SESSION_ICON); //4lines
        } catch (Exception e) {
            fail("Failed to load session tab" + e.getClass() + ": " + e.getLocalizedMessage());
        }
        try {
            CourseNavigationUtilities.go2Tab(driver, FORUM_ICON); //4lines
        } catch (Exception e) {
            fail("Failed to load forum tab" + e.getClass() + ": " + e.getLocalizedMessage());
        }
        try {
            CourseNavigationUtilities.go2Tab(driver, FILES_ICON); //4lines
        } catch (Exception e) {
            fail("Failed to load files tab" + e.getClass() + ": " + e.getLocalizedMessage());
        }
        try {
            CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON); //4lines
        } catch (Exception e) {
            fail("Failed to load attenders tab" + e.getClass() + ": " + e.getLocalizedMessage());
        }
    }
    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "calendarservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    void userAccessCalendarFS4oTest(String mail, String password, String role) {
        this.slowLogin(user,mail, password);
        try {
            // Navigate to the calendar page
            NavigationUtilities.toCoursesHome(driver);
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("calendar-div")));

            // Verify that the calendar displays all the user's classes
            List<WebElement> classes = driver.findElements(By.className("cal-day-badge"));
            assertTrue(classes.size() > 0, "No classes found in the calendar");

        } catch (Exception e) {
            fail("Failed to access the calendar: " + e.getClass() + ": " + e.getLocalizedMessage());
        }
    }
    @ParameterizedTest
    @MethodSource("data")
    void userCanAccessAndViewCalendarCOT4oTest(String mail, String password, String role) {
        this.slowLogin(user,mail, password);

        try {
            // Navigate to the calendar page
            NavigationUtilities.toCoursesHome(driver);
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("calendar-div")));

            // Verify that the calendar is displayed
            WebElement calendar = driver.findElement(By.className("calendar-div"));
            assertTrue(calendar.isDisplayed(), "Calendar is not displayed");

            // Verify that the calendar displays all the user's classes
            List<WebElement> classes = calendar.findElements(By.className("cal-day-badge"));
            assertTrue(classes.size() > 0, "No classes are displayed on the calendar");

            for (WebElement calendarClass : classes) {
                assertTrue(calendarClass.isDisplayed(), "Class is not displayed correctly on the calendar");
            }

        } catch (Exception e) {
            fail("Failed to access and view the calendar: " + e.getMessage());
        } finally {
            // Teardown
            driver.get(APP_URL);
        }
    }
    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "calendar", concurrency = 1, sharing = true, accessMode = "READONLY")
    @ParameterizedTest
    @MethodSource("data")
    @DisplayName("User Accessing Calendar Test")
    void userAccessingCalendarFS4oMiniTest(String mail, String password, String role) {
        // Step 1: User logs into the application
        this.slowLogin(user, mail, password);

        try {
            // Step 2: User navigates to the calendar page
            NavigationUtilities.toCoursesHome(driver);
            // Step 3: System displays a calendar with all the user's classes
            List<WebElement> calendarEntries = driver.findElements(By.className("cal-day-badge")); // Assuming 'calendar-entry' is the class for calendar entries
            assertFalse(calendarEntries.isEmpty(), "The calendar should display at least one class entry.");

            // Additional assertions can be added here to verify the content of the calendar entries
                Click.element(user.getDriver(),calendarEntries.get(0));
                String entrytitle=user.getDriver().findElement(By.className("cal-event-title")).getText();
                assertEquals("Session 1: Introduction to Web | 12:41",entrytitle,"Class title should be present in the calendar entry.");


        } catch (ElementNotFoundException notFoundException) {
            fail("Failed to navigate to the calendar page:: " + notFoundException.getClass() + ": " + notFoundException.getLocalizedMessage());
        } finally {
            // Teardown: Logout or navigate back to the main page
            user.getDriver().get(APP_URL);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    void userCanAccessAndViewCalendarCOT4MiniTest(String mail, String password, String role) {
        // Step 1: User logs into the application
        this.slowLogin(user, mail, password);

        try {
            // Step 2: User navigates to the calendar page
            NavigationUtilities.toCoursesHome(driver);

            // Step 3: Verify calendar display
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("calendar-div")));
            WebElement calendar = driver.findElement(By.className("calendar-div"));
            assertTrue(calendar.isDisplayed(), "Calendar is not displayed");

            // Step 4: Check that the calendar shows all the user's classes
            List<WebElement> classes =  calendar.findElements(By.className("cal-day-badge")); ;
            assertTrue(classes.size() > 0, "No classes found in the calendar");

            // Optionally, you can validate the class details here
            // For example, check if the classes match expected classes for the user

        } catch (Exception e) {
            Assertions.fail("Failed to access and view the calendar: " + e.getMessage());
        } finally {
            // Step 5: Teardown if necessary
            user.getDriver().get(APP_URL); // Navigate back to the main page or logout
        }
    }

}
