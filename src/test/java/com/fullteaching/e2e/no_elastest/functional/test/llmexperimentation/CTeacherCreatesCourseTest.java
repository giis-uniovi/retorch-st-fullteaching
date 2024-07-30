package com.fullteaching.e2e.no_elastest.functional.test.llmexperimentation;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import giis.retorch.annotations.AccessMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities.checkIfCourseExists;
import static org.junit.jupiter.api.Assertions.*;

@Tag("e2e")
@DisplayName("E2E tests for FullTeaching Login Session")
class CTeacherCreatesCourseTest extends BaseLoggedTest {
    protected final String courseName = "Pseudoscientific course for treating the evil eye";
    protected final String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

    public CTeacherCreatesCourseTest() {
        super();
    }

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }

    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "openvidumock", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @AccessMode(resID = "course", concurrency = 15, sharing = true, accessMode = "DYNAMIC")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    void teacherCreateAndDeletecourseTest (String mail, String password, String role) throws ElementNotFoundException {
        // Setup
        this.slowLogin(user, mail, password);

        // Create a new course
        String courseTitle = "Test Course_" + System.currentTimeMillis();
        CourseNavigationUtilities.newCourse(user.getDriver(), courseTitle);
        //TO-DO the problem its here
        // Verify the course has been created
        assertTrue(checkIfCourseExists(driver, courseTitle));

        // Delete the course
        CourseNavigationUtilities.deleteCourse(user.getDriver(), courseTitle);

        // Verify the course has been deleted
        assertFalse(checkIfCourseExists(driver, courseTitle));

        // Teardown
        user.getDriver().get(APP_URL);
    }
    @Test
    void teacherCreateCourseFS4oTest() {
        String teacherEmail = "teacher@gmail.com"; // Replace with actual test data
        String teacherPassword = "pass"; // Replace with actual test data
        this.slowLogin(user, teacherEmail, teacherPassword);

        try {
            // Navigate to the dashboard
            NavigationUtilities.toCoursesHome(driver);

            // Click on the "Create Course" button
            WebElement createCourseButton = driver.findElement(By.id("add-course-icon"));
            Click.element(driver, createCourseButton);

            // Enter course title
            WebElement courseTitleInput = driver.findElement(By.id("input-post-course-name"));
            courseTitleInput.sendKeys("New Course Title");

            // Optionally upload an image
            WebElement fileInput = driver.findElement(By.id("inputPostCourseImage"));
            File uploadFile= new File("src/test/resources/inputs/image.png");
            fileInput.sendKeys(uploadFile.getAbsolutePath());

            // Click on the "Create Course" button
            WebElement createCourseSubmitButton = driver.findElement(By.id("submit-post-course-btn"));
            Click.element(driver, createCourseSubmitButton);

            // Wait for the course to appear in the dashboard
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("course-list")));

            // Verify the new course appears in the teacher's dashboard
            WebElement newCourse = driver.findElement(By.xpath("//*[contains(text(), 'New Course Title')]"));
            assertTrue(newCourse.isDisplayed(), "New course is not displayed in the dashboard");

        } catch (Exception e) {
            fail("Failed to create a new course: " + e.getClass() + ": " + e.getLocalizedMessage());
        }
    }
    @Test
    void teacherCreateCourseCOT4oTest() {
        String teacherEmail = "teacher@gmail.com"; // Replace with actual test data
        String teacherPassword = "pass"; // Replace with actual test data
        this.slowLogin(user, teacherEmail, teacherPassword);

        try {
            // Navigate to the dashboard
            NavigationUtilities.toCoursesHome(driver);

            // Click on the "Create Course" button
            WebElement createCourseButton = driver.findElement(By.id("add-course-icon"));
            Click.element(driver, createCourseButton);

            // Enter course title
            WebElement courseTitleInput = driver.findElement(By.id("input-post-course-name"));
            courseTitleInput.sendKeys("New Course Title");

            // Optionally upload an image
            WebElement fileInput = driver.findElement(By.id("inputPostCourseImage"));
            File uploadFile= new File("src/test/resources/inputs/image.png");
            fileInput.sendKeys(uploadFile.getAbsolutePath());

            // Click on the "Create Course" button
            WebElement createCourseSubmitButton = driver.findElement(By.id("submit-post-course-btn"));
            Click.element(driver, createCourseSubmitButton);

            // Wait for the course to appear in the dashboard
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("course-list")));

            // Verify the new course appears in the teacher's dashboard
            WebElement newCourse = driver.findElement(By.xpath("//*[contains(text(), 'New Course Title')]"));
            assertTrue(newCourse.isDisplayed(), "New course is not displayed in the dashboard");

        } catch (Exception e) {
            fail("Failed to create a new course: " + e.getClass() + ": " + e.getLocalizedMessage());
        }
    }
    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "course", concurrency = 1, sharing = true, accessMode = "READWRITE")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    @Test
    void teacherCreatesCourseFS4oMiniTest() {
        String teacherEmail = "teacher@gmail.com"; // Replace with actual test data
        String teacherPassword = "pass"; // Replace with actual test data
        // Log in with provided credentials
        this.slowLogin(user, teacherEmail, teacherPassword);
        String courseTitle = "New Course Title";

        try {
            // Navigate to course creation page
            NavigationUtilities.toCoursesHome(driver);
            // Press the add course button
            WebElement createCourseButton = driver.findElement(By.id("add-course-icon"));
            Click.element(driver, createCourseButton);
            // Enter course title
            WebElement titleInput = driver.findElement(By.id("input-post-course-name"));// Adjust the locator as necessary
            titleInput.sendKeys(courseTitle);

            // Optionally upload an image
            WebElement fileInput = driver.findElement(By.id("inputPostCourseImage"));
            File uploadFile= new File("src/test/resources/inputs/image.png");
            fileInput.sendKeys(uploadFile.getAbsolutePath());

            // Click on "Create Course" button
            WebElement createCourseSubmitButton = driver.findElement(By.id("submit-post-course-btn"));
            createCourseSubmitButton.click();
            // Wait for the course to be created and displayed in the dashboard
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + courseTitle + "')]"))); // Adjust the locator as necessary

            // Verify that the new course appears in the teacher's dashboard
            List<WebElement> courses = driver.findElements(By.xpath("//*[contains(@class, 'course-title')]")); // Adjust the locator as necessary
            boolean courseCreated = courses.stream().anyMatch(course -> course.getText().equals(courseTitle));
            assertTrue(courseCreated, "The course was not created successfully and does not appear in the dashboard.");

        } catch (ElementNotFoundException notFoundException) {
            fail("Failed to create course:: " + notFoundException.getClass() + ": " + notFoundException.getLocalizedMessage());
        }
    }

    @Test
    void teacherCreatesCourseCOT4oMiniTest() throws ElementNotFoundException {
        // Test data
        String teacherEmail = "teacher@gmail.com"; // Replace with actual test data
        String teacherPassword = "pass"; // Replace with actual test data
        String courseTitle = "Test Course Title"; // Replace with desired course title

        // Step 1: Login as Teacher
        this.slowLogin(user, teacherEmail, teacherPassword);

        // Step 2: Navigate to Course Creation Page
        Click.element(driver, By.id("add-course-icon")); // Replace with actual button ID

        // Step 3: Fill in Course Details
        WebElement titleInput = driver.findElement(By.id("input-post-course-name")); // Replace with actual input ID
        titleInput.sendKeys(courseTitle);

        // Optional: Upload an image if applicable
        WebElement fileInput = driver.findElement(By.id("inputPostCourseImage"));
        File uploadFile= new File("src/test/resources/inputs/image.png");
        fileInput.sendKeys(uploadFile.getAbsolutePath());

        Click.element(driver, By.id("submit-post-course-btn")); // Replace with actual submit button ID

        // Step 4: Verify Course Creation
        Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("dashboard-title"))); // Wait for dashboard to load
        List<WebElement> courses = driver.findElements(By.xpath("//*[contains(text(), '" + courseTitle + "')]")); // Replace with actual class name
        boolean courseCreated = courses.stream().anyMatch(course -> course.getText().equals(courseTitle));

        Assertions.assertTrue(courseCreated, "The course was not created successfully.");

        // Step 5: Teardown
        //Click.element(driver, By.id("logout-button")); // Replace with actual logout button ID Its done by the base Test Class (n
    }

}
