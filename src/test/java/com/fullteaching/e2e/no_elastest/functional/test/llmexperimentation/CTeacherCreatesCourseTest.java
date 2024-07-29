package com.fullteaching.e2e.no_elastest.functional.test.llmexperimentation;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @ParameterizedTest
    @MethodSource("data")
    void teacherCreateCourseFS4oTest(String mail, String password, String role) {
        this.slowLogin(user, mail, password);

        try {
            // Navigate to the dashboard
            NavigationUtilities.toDashboard(driver);

            // Click on the "Create Course" button
            WebElement createCourseButton = driver.findElement(By.id("create-course-button"));
            Click.element(driver, createCourseButton);

            // Enter course title
            WebElement courseTitleInput = driver.findElement(By.id("course-title-input"));
            courseTitleInput.sendKeys("New Course Title");

            // Optionally upload an image
            WebElement courseImageInput = driver.findElement(By.id("course-image-input"));
            courseImageInput.sendKeys("/path/to/image.jpg");

            // Click on the "Create Course" button
            WebElement createCourseSubmitButton = driver.findElement(By.id("create-course-submit-button"));
            Click.element(driver, createCourseSubmitButton);

            // Wait for the course to appear in the dashboard
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("course-list")));

            // Verify the new course appears in the teacher's dashboard
            WebElement newCourse = driver.findElement(By.xpath("//div[contains(text(), 'New Course Title')]"));
            assertTrue(newCourse.isDisplayed(), "New course is not displayed in the dashboard");

        } catch (Exception e) {
            fail("Failed to create a new course: " + e.getClass() + ": " + e.getLocalizedMessage());
        }
    }
    @ParameterizedTest
    @MethodSource("data")
    void teacherCreateCourseCOT4oTest(String mail, String password, String role) {
        this.slowLogin(user, mail, password);

        try {
            // Navigate to the dashboard
            NavigationUtilities.toDashboard(driver);

            // Click on the "Create Course" button
            WebElement createCourseButton = driver.findElement(By.id("create-course-button"));
            Click.element(driver, createCourseButton);

            // Enter course title
            WebElement courseTitleInput = driver.findElement(By.id("course-title-input"));
            courseTitleInput.sendKeys("New Course Title");

            // Optionally upload an image
            WebElement courseImageInput = driver.findElement(By.id("course-image-input"));
            courseImageInput.sendKeys("/path/to/image.jpg");

            // Click on the "Create Course" button
            WebElement createCourseSubmitButton = driver.findElement(By.id("create-course-submit-button"));
            Click.element(driver, createCourseSubmitButton);

            // Wait for the course to appear in the dashboard
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("course-list")));

            // Verify the new course appears in the teacher's dashboard
            WebElement newCourse = driver.findElement(By.xpath("//div[contains(text(), 'New Course Title')]"));
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
    @ParameterizedTest
    @MethodSource("data")
    void teacherCreatesCourseFS4oMiniTest(String mail, String password, String role) {
        // Log in with provided credentials
        this.slowLogin(user, mail, password);
        String courseTitle = "New Course Title";
        String courseImagePath = "path/to/image.jpg"; // Optional image path

        try {
            // Navigate to course creation page
            NavigationUtilities.toCourseCreationPage(driver);

            // Enter course title
            WebElement titleInput = driver.findElement(By.id("courseTitleInput")); // Adjust the locator as necessary
            titleInput.sendKeys(courseTitle);

            // Optionally upload an image
            if (courseImagePath != null && !courseImagePath.isEmpty()) {
                WebElement imageUploadInput = driver.findElement(By.id("courseImageUpload")); // Adjust the locator as necessary
                imageUploadInput.sendKeys(courseImagePath);
            }

            // Click on "Create Course" button
            WebElement createCourseButton = driver.findElement(By.id("createCourseButton")); // Adjust the locator as necessary
            Click.element(driver, createCourseButton);

            // Wait for the course to be created and displayed in the dashboard
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), '" + courseTitle + "')]"))); // Adjust the locator as necessary

            // Verify that the new course appears in the teacher's dashboard
            List<WebElement> courses = driver.findElements(By.xpath("//div[contains(@class, 'course-title')]")); // Adjust the locator as necessary
            boolean courseCreated = courses.stream().anyMatch(course -> course.getText().equals(courseTitle));
            assertTrue(courseCreated, "The course was not created successfully and does not appear in the dashboard.");

        } catch (ElementNotFoundException notFoundException) {
            fail("Failed to create course:: " + notFoundException.getClass() + ": " + notFoundException.getLocalizedMessage());
        }
    }

    @Test
    void teacherCreatesCourseCOT4oMiniTest() {
        // Test data
        String teacherEmail = "teacher@example.com"; // Replace with actual test data
        String teacherPassword = "password"; // Replace with actual test data
        String courseTitle = "Test Course Title"; // Replace with desired course title
        String courseImagePath = "path/to/image.jpg"; // Replace with actual image path if needed

        // Step 1: Login as Teacher
        this.slowLogin(user, teacherEmail, teacherPassword);

        // Step 2: Navigate to Course Creation Page
        NavigationUtilities.toDashboard(driver);
        Click.element(driver, By.id("create-course-button")); // Replace with actual button ID

        // Step 3: Fill in Course Details
        WebElement titleInput = driver.findElement(By.id("course-title")); // Replace with actual input ID
        titleInput.sendKeys(courseTitle);

        // Optional: Upload an image if applicable
        if (courseImagePath != null && !courseImagePath.isEmpty()) {
            WebElement imageUpload = driver.findElement(By.id("course-image-upload")); // Replace with actual upload ID
            imageUpload.sendKeys(courseImagePath);
        }

        Click.element(driver, By.id("create-course-submit")); // Replace with actual submit button ID

        // Step 4: Verify Course Creation
        Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id("dashboard"))); // Wait for dashboard to load
        List<WebElement> courses = driver.findElements(By.className("course-title")); // Replace with actual class name
        boolean courseCreated = courses.stream().anyMatch(course -> course.getText().equals(courseTitle));

        Assertions.assertTrue(courseCreated, "The course was not created successfully.");

        // Step 5: Teardown
        Click.element(driver, By.id("logout-button")); // Replace with actual logout button ID
    }

}
