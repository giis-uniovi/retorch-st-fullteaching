package com.fullteaching.e2e.no_elastest.functional.test.media;

import com.fullteaching.e2e.no_elastest.common.*;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.slf4j.LoggerFactory.getLogger;

class FullTeachingLoggedVideoSessionTests extends BaseLoggedTest {


    final static Logger log = getLogger(lookup().lookupClass());
    protected final String host = LOCALHOST;
    public String users_data;
    public String courseName;
    //1 teacher
    //at least 1 student;
    protected List<BrowserUser> studentDriver;
    protected List<String> studentPass;
    protected List<String> studentNames;
    protected List<String> students;
    String TestName = "default-test-name";

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
    @Test
    void sessionTest() { // 160 +225+ 28 set up +13 lines teardown =426
        courseName = "Pseudoscientific course for treating the evil eye";

        users_data = loadStudentsData("src/test/resources/inputs/default_user_LoggedVideoStudents.csv");
        this.user = setupBrowser(CHROME, TJOB_NAME + "_" + TEST_NAME, "TEACHER", WAIT_SECONDS);//27 lines
        this.slowLogin(user, "teacher@gmail.com", "pass");//24 lines
        //students setUp
        students = new ArrayList<>();
        studentPass = new ArrayList<>();
        studentNames = new ArrayList<>();
        studentDriver = new ArrayList<>();
        String[] students_data = users_data.split(";");
        for (int i = 0; i < students_data.length; i++) {
            String userid = students_data[i].split(":")[0];
            students.add(userid);
            String user_password = students_data[i].split(":")[1];
            studentPass.add(user_password);
            String STUDENT_BROWSER = students_data[i].split(":")[2];
            //WebDriver studentD = UserLoader.allocateNewBrowser(students_data[i].split(":")[2]);
            BrowserUser studentD = setupBrowser(STUDENT_BROWSER, TJOB_NAME + "_" + TEST_NAME, "STUDENT" + i, WAIT_SECONDS); //27 lines
            this.slowLogin(studentD, userid, user_password); //24 lines
            studentNames.add(userid);
            studentDriver.add(studentD);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR);
        if (mHour == 0) mHour = 12;
        int mAMPM = calendar.get(Calendar.AM_PM);
        int mMinute = calendar.get(Calendar.MINUTE);
        String sessionDate = "" + (mDay < 10 ? "0" + mDay : mDay) + (mMonth < 10 ? "0" + mMonth : mMonth) + mYear;
        String sessionHour = "" + (mHour < 10 ? "0" + mHour : mHour) + (mMinute < 10 ? "0" + mMinute : mMinute) + (mAMPM == Calendar.AM ? "A" : "P");
        String sessionName = "Today's Session";
        try {
            List<String> courses = CourseNavigationUtilities.getCoursesList(user.getDriver(), host); //13 lines
            assertTrue(courses.size() > 0, "No courses in the list");
            //Teacher go to Course and create a new session to join
            WebElement course = CourseNavigationUtilities.getCourseElement(user.getDriver(), courseName); //14 lines
            course.findElement(COURSE_LIST_COURSE_TITLE).click();
            Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
            CourseNavigationUtilities.go2Tab(user.getDriver(), SESSION_ICON); //4 lines
            Click.element(user.getDriver(), SESSION_LIST_NEW_SESSION_ICON);
            //wait for modal
            WebElement modal = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(SESSION_LIST_NEW_SESSION_MODAL));
            modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_TITLE).sendKeys(sessionName);
            String sessionDescription = "Wow today session will be amazing";
            modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_CONTENT).sendKeys(sessionDescription);
            modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_DATE).sendKeys(sessionDate);
            modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_TIME).sendKeys(sessionHour);
            Click.element(user.getDriver(), modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_POST_BUTTON));
            Wait.notTooMuch(user.getDriver());

            //check if session has been created
            Wait.waitForPageLoaded(user.getDriver()); //13 lines
            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver());
            assertTrue(session_titles.contains(sessionName), "Session has not been created");

            //Teacher Join Session

            session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver());
            assertTrue(session_titles.contains(sessionName), "Session has not been created");
            //Teacher to: JOIN SESSION.
            WebElement session = SessionNavigationUtilities.getSession(user.getDriver(), sessionName); //17 lines
            Click.element(user.getDriver(), session.findElement(SESSION_LIST_SESSION_ACCESS));
            //assertTrue(condition);
            //Check why this is failing... maybe urls are not correct? configuration on the project?

            for (BrowserUser student_d : studentDriver) {
                WebDriver studentDriver = student_d.getDriver();
                if (NavigationUtilities.amINotHere(studentDriver, COURSES_URL.replace("__HOST__", host))) {
                    NavigationUtilities.toCoursesHome(studentDriver); //3lines
                }
                courses = CourseNavigationUtilities.getCoursesList(studentDriver, host);//13 lines
                assertTrue(courses.size() > 0, "No courses in the list");
                //Teacher go to Course and create a new session to join
                course = CourseNavigationUtilities.getCourseElement(studentDriver, courseName); //14 lines
                course.findElement(COURSE_LIST_COURSE_TITLE).click();
                Wait.notTooMuch(studentDriver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
                CourseNavigationUtilities.go2Tab(studentDriver, SESSION_ICON);//4lines
                session_titles = SessionNavigationUtilities.getFullSessionList(studentDriver);//7 lines
                assertTrue(session_titles.contains(sessionName), "Session has not been created");
                //Student to: JOIN SESSION.
                session = SessionNavigationUtilities.getSession(studentDriver, sessionName);//17 lines
                Click.element(studentDriver, session.findElement(SESSION_LIST_SESSION_ACCESS));
                //assertTrue(condition);
                //Check why this is failing... maybe urls are not correct? configuration on the project?
            }
        } catch (ElementNotFoundException e) {
            fail("Error while creating new SESSION");
        }
        //Students Leave Sessions
        try {
            for (BrowserUser student : studentDriver) {
                WebDriver studentDriver = student.getDriver();
                Wait.notTooMuch(studentDriver);
                //student to: LEAVE SESSION.
                studentDriver = Click.element(studentDriver, SESSION_LEFT_MENU_BUTTON);
                Wait.notTooMuch(studentDriver).until(ExpectedConditions.visibilityOfElementLocated(SESSION_EXIT_ICON));
                WebElement exitButton = studentDriver.findElement(SESSION_EXIT_ICON);
                JavascriptExecutor executor = (JavascriptExecutor) studentDriver;
                executor.executeScript("arguments[0].click();", exitButton);
                // studentDriver.findElement(By.id("exit-icon")).click();
                //  studentDriver = Click.element(studentDriver, By.className("right material-icons video-icon"));
                //Wait for something
                Wait.notTooMuch(studentDriver).until(ExpectedConditions.visibilityOfElementLocated(COURSE_TABS));
                //assertTrue(condition);
                //Check why this is failing... maybe urls are not correct? configuration on the project?
            }

            //Teacher Leave Session

            //student to: LEAVE SESSION.
            Click.element(user.getDriver(), SESSION_LEFT_MENU_BUTTON);
            WebElement exitButton = user.getDriver().findElement(SESSION_EXIT_ICON);
            JavascriptExecutor executor = (JavascriptExecutor) user.getDriver();
            executor.executeScript("arguments[0].click();", exitButton);
            Wait.waitForPageLoaded(user.getDriver());//13 lines
            //Wait for something
            Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(COURSE_TABS));
            //assertTrue(condition);
            //Check why this is failing... maybe urls are not correct? configuration on the project?
        } catch (ElementNotFoundException e) {
            fail("Error while leaving SESSION");
        }
        try {
            //delete session by teacher
            WebElement session = SessionNavigationUtilities.getSession(user.getDriver(), sessionName);
            Click.element(user.getDriver(), session.findElement(SESSION_LIST_SESSION_EDIT_ICON));
            WebElement modal = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(SESSION_LIST_EDIT_MODAL));
            Click.element(user.getDriver(), modal.findElement(SESSION_LIST_EDIT_MODAL_DELETE_DIV).findElement(By.tagName("label")));
            Click.element(user.getDriver(), modal.findElement(SESSION_LIST_EDIT_MODAL_DELETE_DIV).findElement(By.tagName("a")));
            Wait.waitForPageLoaded(user.getDriver());//13 lines
            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver()); //7 lines
            assertFalse(session_titles.contains(sessionName), "Session has not been deleted");
        } catch (ElementNotFoundException e) {

            e.printStackTrace();
        }

    }


    public String loadStudentsData(String path) { //17 lines
        FileReader file;
        StringBuilder key = new StringBuilder();
        try {
            file = new FileReader(path);
            BufferedReader reader = new BufferedReader(file);
            // **** key is declared here in this block of code
            String line = reader.readLine();
            while (line != null) {
                key.append(line);
                line = reader.readLine();
            }
            System.out.println(key); // so key works
        } catch (IOException e) {

            e.printStackTrace();
        }
        return key.toString();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) { //13 lines

        if (testInfo.getTestMethod().isPresent()) {
            TestName = testInfo.getTestMethod().get().getName();
        }
        if (user != null) {
            log.info("##### Finishing OpenVidu Video test: {} - Driver {}", TestName, this.user.getDriver());
            log.info("Browser console at the end of the test");
            LogEntries logEntries = user.getDriver().manage().logs().get(BROWSER);
            logEntries.forEach((entry) -> log.info("[{}] {} {}",
                    new Date(entry.getTimestamp()), entry.getLevel(),
                    entry.getMessage()));
            //TO-DO- ERROR with the logout


            //Logout and exit students
            for (BrowserUser student : studentDriver) {
                if (student.isOnSession()) {
                    this.logout(student);
                }

                student.dispose();
            }
            //logout and exist teacher
            this.logout(user);
            user.dispose();
        }
    }
}
