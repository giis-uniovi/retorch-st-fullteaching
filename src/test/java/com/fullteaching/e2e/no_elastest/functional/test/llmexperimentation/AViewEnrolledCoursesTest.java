package com.fullteaching.e2e.no_elastest.functional.test.llmexperimentation;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.DOMManager;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import org.junit.jupiter.api.Assertions;
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
class AViewEnrolledCoursesTest extends BaseLoggedTest {
    protected final String courseName = "Pseudoscientific course for treating the evil eye";
    protected final String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

    public AViewEnrolledCoursesTest() {
        super();
    }

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }

    /**
     * This test get login and navigate to the courses zone checking if there are
     * any courses. Second and go to the Pseudo... course accessing to the forum
     * and looks if its enable.If It's enable, load all the entries and checks for
     * someone that have comments on it.Finally, with the two previous conditions,
     * makes an assertEquals() to ensure that both are accomplishment
     */
    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "openvidumock", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @AccessMode(resID = "forum", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    void forumLoadEntriesOriginalTest(String mail, String password, String role) {
        this.slowLogin(user, mail, password);
        try {
            //navigate to courses.
            NavigationUtilities.toCoursesHome(driver);
            List<String> courses = CourseNavigationUtilities.getCoursesList(driver);
            assertTrue(courses.size() > 0, "No courses in the list");
            //find course with forum activated
            boolean activated_forum_on_some_test = false;
            boolean has_comments = false;
            for (String course_name : courses) {
                //go to each of the courses
                WebElement course = CourseNavigationUtilities.getCourseByName(driver, course_name);
                course.findElement(COURSE_LIST_COURSE_TITLE).click();
                Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
                log.info("Navigating to the forum and checking if its enabled");
                //go to forum tab to check if enabled:
                //load forum
                CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
                if (ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver, FORUM_ICON))) {
                    activated_forum_on_some_test = true;
                    log.info("Loading the entries list");
                    //Load list of entries
                    List<String> entries_list = ForumNavigationUtilities.getFullEntryList(driver);
                    if (entries_list.size() > 0) {
                        //Go into first entry
                        for (String entry_name : entries_list) {
                            log.info("Checking the entry with name: {}", entry_name);
                            WebElement entry = ForumNavigationUtilities.getEntry(driver, entry_name);
                            Click.element(driver, entry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE));
                            //Load comments
                            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST));
                            List<WebElement> comments = ForumNavigationUtilities.getComments(driver);
                            log.info("Checking if the entry has comments");
                            if (comments.size() > 0) {
                                has_comments = true;
                                log.info("Comments found, saving them");

                                ForumNavigationUtilities.getUserComments(driver, userName);
                            }//else go to next entry
                            Click.element(driver, DOMManager.getParent(driver, driver.findElement(BACK_TO_ENTRIES_LIST_ICON)));
                        }
                    }//(else) if no entries go to next course
                }//(else) if forum no active go to next course
                log.info("Returning to the main dashboard");
                driver = Click.element(driver, BACK_TO_DASHBOARD);
            }
            assertTrue((activated_forum_on_some_test && has_comments), "There isn't any forum that can be used to test this [Or not activated or no entry lists or not comments]");
        } catch (ElementNotFoundException notFoundException) {
            fail("Failed to navigate to courses forum:: " + notFoundException.getClass() + ": " + notFoundException.getLocalizedMessage());
        }
    }
    @ParameterizedTest
    @MethodSource("data")
    void viewEnrolledCoursesFS4oTest(String mail, String password, String role) {
        // Step 1: User logs into the application
        this.slowLogin(user, mail, password);

        // Step 2: User navigates to the dashboard
        try {
            if (NavigationUtilities.amINotHere(driver, COURSES_URL.replace("__HOST__", HOST))) {
                NavigationUtilities.toCoursesHome(driver);
            }

            // Step 3: System displays a list of courses the user is enrolled in
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("course-list")));
            List<WebElement> courses = driver.findElements(By.className("course-list-item"));

            // Expected Output: The dashboard shows a list of courses the user is enrolled in
            assertTrue(courses.size() > 0, "No enrolled courses found for the user");

        } catch (Exception e) {
            fail("Failed to view enrolled courses: " + e.getClass() + ": " + e.getLocalizedMessage());
        }

        // Teardown: Navigate to the main page to logout
        user.getDriver().get(APP_URL);
    }
    @ParameterizedTest
    @MethodSource("data")
    void viewEnrolledCoursesCOT4oTest(String email, String password, String role) {
        // Setup
        String[] expectedCourses = {"Pseudoscientific course for treating the evil eye",  "Don't mind. This is a real course"};

        // Login
        this.slowLogin(user, email, password);

        // Verify Dashboard
        try {
            NavigationUtilities.toCoursesHome(driver);
            // Wait for the dashboard to load
            Wait.notTooMuch(driver).until(ExpectedConditions.presenceOfElementLocated(COURSES_DASHBOARD_TITLE));

            // Get the list of enrolled courses
            List<WebElement> courses = driver.findElements(By.className("title"));

            // Verify the number of courses
            assertEquals(expectedCourses.length, courses.size(), "Number of enrolled courses does not match");

            // Verify the course titles
            for (int i = 0; i < expectedCourses.length; i++) {
                assertEquals(expectedCourses[i], courses.get(i).getText(), "Course title does not match");
            }

        } catch (Exception e) {
            fail("Failed to verify enrolled courses: " + e.getClass() + ": " + e.getLocalizedMessage());
        }

        // Teardown
        user.getDriver().get(APP_URL);
    }
    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "course", concurrency = 15, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    void viewEnrolledCoursesFS4oMiniTest(String mail, String password, String role) throws ElementNotFoundException {
        // Setup: User logs into the application
        this.slowLogin(user, mail, password);

        // Navigate to the dashboard
        NavigationUtilities.toCoursesHome(driver);

        // Wait for the courses list to be visible
        Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("course-list")));

        // Verify that the dashboard shows a list of courses the user is enrolled in
        List<WebElement> enrolledCourses = driver.findElements(By.className("course-list-item"));
        assertFalse(enrolledCourses.size()!=2, "The enrolled courses list should not be empty.");

        // Optionally, you can check for specific course titles if known
        // assertTrue(enrolledCourses.stream().anyMatch(course -> course.getText().equals("Expected Course Title")), "Expected course is not found in the enrolled courses list.");

        // Teardown: Navigate back to the main page or logout
        user.getDriver().get(APP_URL);
    }
    @ParameterizedTest
    @MethodSource("data")
    void viewEnrolledCoursesCOT4oMiniTest(String email, String password, String role) throws ElementNotFoundException {
        // Setup: Log in to the application
        this.slowLogin(user, email, password);

        // Navigation: Navigate to the dashboard
        NavigationUtilities.toCoursesHome(driver);

        // Verification: Check that the system displays a list of enrolled courses
        List<WebElement> enrolledCourses = driver.findElements(By.className("course-list-item")); // Replace with actual class name for enrolled courses
        Assertions.assertFalse(enrolledCourses.isEmpty(), "The enrolled courses list should not be empty.");

        // Optionally, validate the content of the enrolled courses
        for (WebElement course : enrolledCourses) {
            String courseTitle = course.getText(); // Assuming the course title is displayed as text
            Assertions.assertNotNull(courseTitle, "Course title should not be null.");
            // Add more assertions as needed to validate course details
        }

        // Teardown: Log out of the application
        //NavigationUtilities.logout(driver); // Replace with actual logout method (NOT NECESSARY DONE IN THE TEAR-DOWN)
    }


}
