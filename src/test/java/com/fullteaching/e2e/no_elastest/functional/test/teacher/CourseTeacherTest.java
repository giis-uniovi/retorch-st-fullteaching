package com.fullteaching.e2e.no_elastest.functional.test.teacher;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.ExceptionsHelper;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities.checkIfCourseExists;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SeleniumJupiter.class)
class CourseTeacherTest extends BaseLoggedTest {

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }

    /**
     * This test get the login the user, go  the courses and select the default
     * course.Once the user it's here, it clicks upon the different tabs(Course info,sessions,Forum,Files
     * and attenders), checking that the navigation its possible.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "READONLY")
    @ParameterizedTest
    @MethodSource("data")
    void teacherCourseMainTest(String mail, String password, String role) {//39+80+ 28 set up +13 lines teardown =160
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

    /**
     * This test get the login the user, go  the courses  and press the button of a
     * new course, creating a new course(each course have a time stamp that avoids
     * overlapping between different test).After that, we proceed to delete those courses, click
     * into the edit icon, check the box that allows it and clicking into the delete button
     * After that, we proceed to check if the course don't appear in the list.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "DYNAMIC")
    @ParameterizedTest
    @MethodSource("data")
    void teacherCreateAndDeleteCourseTest(String mail, String password, String role) throws ElementNotFoundException {
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



    /**
     * This test get the login the user, go to the courses  and in first place, edits the
     * course title, change its name for EDIT_+ one timestamp to avoid test overlapping.After that, we proceed
     * to edit course details, deletes the details title, subtitle and content adding new values
     * for those fields.Second checks if this content was correctly added.In second place, checks if the
     * course forum is enabled/disabled, to proceed to check if allows disabling/enable it.Finally,
     * this test check if the current user, that is accessing to the course, is included
     * in the Attenders list( check if the user is in it)
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Configuration"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    void teacherEditCourseValues(String mail, String password, String role) {//165+256+ 28 set up +13 lines teardown =462
        String courseName = properties.getProperty("forum.test.course");
        this.slowLogin(user, mail, password); //24 lines
        try {
            // navigate to course if not there
            NavigationUtilities.toCoursesHome(driver);//3lines
        } catch (Exception e) {
            fail("Failed to go to Courses " + e.getClass() + ": " + e.getLocalizedMessage());
        }
        // select first course (never mind which course -while application is in a test environment-)
        // for more general testing use NavigationUtilities.newCourse, but it will need some code rewriting.
        //Modify name

        try {
            WebElement course = CourseNavigationUtilities.getCourseByName(driver, courseName); //14lines
            String old_name = course.findElement(By.className("title")).getText();

            String edition_name = "EDITION TEST_" + System.currentTimeMillis();
            log.info("Modifying Course name from {} to {}", old_name, edition_name);
            driver = CourseNavigationUtilities.changeCourseName(driver, old_name, edition_name);//21 lines
            //check if course exists
            log.info("Checking if course exists");
            assertTrue(checkIfCourseExists(driver, edition_name, 3), "The course title hasn't been found in the list ¿Have been created?");//10 lines
            //return to old name
            log.info("Rolling back to old name");
            driver = CourseNavigationUtilities.changeCourseName(driver, edition_name, old_name); //21 lines
            assertTrue(checkIfCourseExists(driver, old_name, 3), "The course title hasn't been reset"); //10 lines
        } catch (Exception e) {
            fail("Failed to edit course name " + e.getClass() + ": " + e.getLocalizedMessage());
        }
        //Go to details and edit them
        try {//*[@id="sticky-footer-div"]/com.fullteaching.e2e.no_elastest.main/app-dashboard/div/div[3]/div/div[1]/ul/li[1]/div/div[2]
            log.info("Going to details and edit them");
            WebElement course = CourseNavigationUtilities.getCourseByName(driver, courseName);//14 lines
            course.findElement(COURSE_LIST_COURSE_TITLE).click();
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
        } catch (Exception e) {
            fail("Failed to load Courses Tabs " + e.getClass() + ": " + e.getLocalizedMessage());
        }
        // Modify description TAB HOME
        try {
            log.info("Modifying the description of TAB HOME");
            CourseNavigationUtilities.go2Tab(driver, HOME_ICON); //4 lines
            //Modify the description
            WebElement edit_description_button = driver.findElement(EDIT_DESCRIPTION_BUTTON);
            Click.element(driver, edit_description_button);
            //wait for editor md editor???'
            WebElement edit_description_desc = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className(EDIT_DESCRIPTION_CONTENT_BOX_CLASS)));
            //text here?? /html/body/app/div/com.fullteaching.e2e.no_elastest.main/app-course-details/div/div[4]/md-tab-group/div[2]/div[1]/div/div[2]/p-editor/div/div[2]/div[1]
            edit_description_desc.getAttribute("ng-reflect-model");
            //delete old_desc
            log.info("Deleting old description");
            WebElement editor = driver.findElement(By.className("ql-editor"));
            editor.sendKeys(SELECT_ALL);
            editor.sendKeys(DELETE);
            //New Title
            log.info("Adding the new description");
            WebElement headerSelector = driver.findElement(By.className("ql-header"));
            Click.element(driver, By.className("ql-header"));
            WebElement picker_options = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-picker-options")));
            WebElement option = NavigationUtilities.getOption(picker_options.findElements(By.className("ql-picker-item")), "Heading", NavigationUtilities.FindOption.ATTRIBUTE, "data-label");//20 lines
            assertNotNull(option, "Something went wrong while setting the Heading");
            Click.element(driver, option);
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            log.info("Writing the new titles: New Title, New SubHeading and This is the normal content");
            //Write the new Title.
            editor = driver.findElement(By.className("ql-editor"));
            jse.executeScript("arguments[0].innerHTML = '<h1>New Title</h1><h2>New SubHeading</h2><p>This is the normal content</p>'", editor);
            driver.findElement(By.xpath("//*[@id=\"textEditorRowButtons\"]/a[2]")).click();
            user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("ql-editor-custom")), "Element that was waiting doesn't found");
            WebElement preview = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-editor-custom")));
            //check heading TO-DO : Error here, the type of font is not saved
            assertEquals("New Title", preview.findElement(By.tagName("h1")).getText(), "Heading preview not properly rendered");
            //check subtitle
            assertEquals("New SubHeading", preview.findElement(By.tagName("h2")).getText(), "Subheading preview not properly rendered");
            //check content
            assertEquals("This is the normal content", preview.findElement(By.tagName("p")).getText(), "Normal preview content not properly rendered");
            //save send-info-btn
            log.info("Checked all the content, click the save button");
            driver.findElement(EDIT_DESCRIPTION_SAVE_BUTTON).click();
            WebElement saved = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-editor-custom")));
            //check heading
            assertEquals("New Title", saved.findElement(By.tagName("h1")).getText(), "Heading saved not properly rendered");
            //check subtitle
            assertEquals("New SubHeading", saved.findElement(By.tagName("h2")).getText(), "Subheading saved not properly rendered");
            //check content
            assertEquals("This is the normal content", saved.findElement(By.tagName("p")).getText(), "Normal content saved not properly rendered");
            log.info("Checking after saving that all content its correct");
        } catch (Exception e) {
            fail("Failed to modify description:: (File:CourseTeacherTest.java - line:" + ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java") + ") "
                    + e.getClass() + ": " + e.getLocalizedMessage());
        }
        // in sessions program
        try {
            CourseNavigationUtilities.go2Tab(driver, SESSION_ICON); //4lines
            // new session ¡in session Tests!
            // delete session ¡in session Tests!
        } catch (Exception e) {
            fail("Failed to test session:: (File:CourseTeacherTest.java - line:" + ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java") + ") "
                    + e.getClass() + ": " + e.getLocalizedMessage());
        }
        // in forum enable/disable
        try {
            log.info("Navigating to Forum");
            CourseNavigationUtilities.go2Tab(driver, FORUM_ICON); //4lines
            WebElement forum_tab_content = CourseNavigationUtilities.getTabContent(driver, FORUM_ICON);
            //check if Forum is enabled

            if (ForumNavigationUtilities.isForumEnabled(forum_tab_content)) {//6lines
                //check entries ¡Only check if there is entries and all the buttons are present!
                log.info("Forum enabled checking that the entries and buttons are present");
                assertNotNull(forum_tab_content.findElement(FORUM_NEW_ENTRY_ICON), "Add Entry not found");
                assertNotNull(forum_tab_content.findElement(FORUM_EDIT_ENTRY_ICON), "Add Entry not found");
                //disable
                ForumNavigationUtilities.disableForum(driver); //12 lines
                //enable
                //click edit
                ForumNavigationUtilities.enableForum(driver); //11 lines
            } else {
                //else
                //enable
                log.info("Forum disabled, enable it and check that the entries and buttons are present");
                ForumNavigationUtilities.enableForum(driver); //11 lines
                //check entries  ¡Only check if there is entries and all the buttons are present!
                assertNotNull(forum_tab_content.findElement(FORUM_NEW_ENTRY_ICON), "Add Entry not found");
                assertNotNull(forum_tab_content.findElement(FORUM_EDIT_ENTRY_ICON), "Add Entry not found");
                //disable
                ForumNavigationUtilities.disableForum(driver); //12 lines
            }
        } catch (Exception e) {
            fail("Failed to tests forum:: (File:CourseTeacherTest.java - line:" + ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java") + ") "
                    + e.getClass() + ": " + e.getLocalizedMessage());
        }
        // in attenders
        try {
            log.info("Checking attenders tab");
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(ATTENDERS_ICON));
            CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON); // 4lines
            CourseNavigationUtilities.getTabContent(driver, ATTENDERS_ICON);
            log.info("Checking if the user is in attenders");
            //is user in attenders?
            assertTrue(CourseNavigationUtilities.isUserInAttendersList(driver, userName), "User isn't in the attenders list"); //15 lines
            //is user highlighted?
            log.info("Checking if the user is highlighted");
            String main_user = CourseNavigationUtilities.getHighlightedAttender(driver); //7 lines
            assertEquals(userName, main_user, "Main user and active user doesn't match");
        } catch (Exception e) {
            fail("Failed to tests attenders:: (File: CourseTeacherTest.java -line: " + ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java") + ") "
                    + e.getClass() + ": " + e.getLocalizedMessage());
        }
        //Currently, at the en of this test the header it's not loaded... This reloads the page and shows the header

        Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#arrow-drop-down")));
        //Well done!
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    void teacherDeleteCourseTest(String mail, String password, String role) throws ElementNotFoundException {//51+114+28 set up +13 lines teardown =206
        this.slowLogin(user, mail, password);//24
        String courseName = "Test Course_" + System.currentTimeMillis();
        // navigate to course if not there
        try {
            NavigationUtilities.toCoursesHome(driver);//3lines
        } catch (Exception e) {
            fail("Failed to go to Courses " + e.getClass() + ": " + e.getLocalizedMessage());
        }
        // create a course
        try {
            log.info("Create a new \"Dummy Course\"");
            Wait.waitForPageLoaded(driver);

            CourseNavigationUtilities.newCourse(driver, courseName);//37 lines
        } catch (ElementNotFoundException e) {
            fail("Failed to create course:: " + e.getClass() + ": " + e.getLocalizedMessage());
        }
        // populate course
        // in sessions program
        // TODO: new session
        // in attenders
        // TODO: add attenders
        // delete course
        List<WebElement> allCoursesPriorDeleting = user.getDriver().findElements(By.className("course-list-item"));
        CourseNavigationUtilities.deleteCourse(user.getDriver(), courseName);
        List<WebElement> allCourses = user.getDriver().findElements(By.className("course-list-item"));
        assertEquals(allCoursesPriorDeleting.size() - 1, allCourses.size());
        //Well done!
    }

}
