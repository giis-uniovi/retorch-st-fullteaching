package com.fullteaching.e2e.no_elastest.functional.test.media;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.BrowserUser;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.SessionNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

class FullTeachingLoggedVideoSessionTests extends BaseLoggedTest {

    final static Logger log = getLogger(lookup().lookupClass());

    public String courseName;
    protected List<String> studentPassList;
    protected List<String> studentNamesList;
    protected List<String> studentNameList;

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }


    /**
     * This method tests the video session functionality of the application.
     * It includes creating a session, joining the session as a teacher and students,
     * leaving the session, and deleting the session.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
    @DisplayName("sessionTest")
    @ParameterizedTest
    @MethodSource("data")
    void sessionTest(String mail, String password, String role) throws ElementNotFoundException, IOException, URISyntaxException {
        String sessionName = "Today's Session";
        courseName = "Pseudoscientific course for treating the evil eye";
        this.slowLogin(this.user, mail, password);
        initializeStudents("src/test/resources/inputs/default_user_LoggedVideoStudents.csv");
        createNewSession(sessionName);
        joinSession(sessionName, this.user);
        joinSessionStudents(sessionName);
        leaveStudentSessions();
        leaveSession(this.user);
        deleteSession(sessionName);
    }

    /**
     * This method initializes the students drivers for the test, reading its data from a CSV file
     * @param pathData the path to the CSV file containing student data
     * @throws IOException if there is an error reading the file
     */
    private void initializeStudents(String pathData) throws IOException, URISyntaxException {
        log.info("Initializing students");
        String users_data = loadStudentsData(pathData);
        studentNameList = new ArrayList<>();
        studentPassList = new ArrayList<>();
        studentNamesList = new ArrayList<>();
        studentBrowserUserList = new ArrayList<>();
        String[] students_data = users_data.split(";");

        for (int i = 0; i < students_data.length; i++) {
            String userid = students_data[i].split(":")[0];
            studentNameList.add(userid);
            String user_password = students_data[i].split(":")[1];
            studentPassList.add(user_password);
            String STUDENT_BROWSER = students_data[i].split(":")[2];
            BrowserUser studentD = setupBrowser(STUDENT_BROWSER, TJOB_NAME + "-" + TEST_NAME, userid, WAIT_SECONDS);
            this.slowLogin(studentD, userid, user_password);
            studentNamesList.add(userid);
            studentBrowserUserList.add(studentD);
        }
        log.info("Initializing students end, number of students: {} " ,studentNameList.size());
    }
    /**
     * This method creates a new video session with the given name, navigating to the course, opens the new session modal,
     * filling the session details and verifying that the session was created.
     * @param sessionName the name of the session to create
     * @throws ElementNotFoundException if an element is not found during navigation
     */
    private void createNewSession(String sessionName) throws ElementNotFoundException {
        String sessionDate = getCurrentDate();
        String sessionHour = getCurrentTime();
        String sessionDescription = "Wow today session will be amazing";

        navigateToCourse(user, courseName);
        Click.element(user.getDriver(), SESSION_LIST_NEW_SESSION_ICON);
        WebElement modal = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(SESSION_LIST_NEW_SESSION_MODAL));
        modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_TITLE).sendKeys(sessionName);
        modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_CONTENT).sendKeys(sessionDescription);
        modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_DATE).sendKeys(sessionDate);
        modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_TIME).sendKeys(sessionHour);
        Click.element(user.getDriver(), modal.findElement(SESSION_LIST_NEW_SESSION_MODAL_POST_BUTTON));
        Wait.notTooMuch(user.getDriver());
        // Verify session creation
        Wait.waitForPageLoaded(user.getDriver());
        user.waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(SESSION_LIST_SESSION_ROW,3),"Incorrect number of sessions (never more than 2)");
        List<String> session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver());
        assertTrue(session_titles.contains(sessionName), "Session has not been created");
    }
    /**
     * This method gets the current date in the format DDMMYYYY. for the video session name
     */
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        return "" + (mDay < 10 ? "0" + mDay : mDay) + (mMonth < 10 ? "0" + mMonth : mMonth) + mYear;
    }
    /**
     * This method gets the current time in the format HHmmA/P, where A/P indicates AM or PM for the video session name
     */
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mHour = calendar.get(Calendar.HOUR);
        if (mHour == 0) mHour = 12;
        int mAMPM = calendar.get(Calendar.AM_PM);
        int mMinute = calendar.get(Calendar.MINUTE);
        return "" + (mHour < 10 ? "0" + mHour : mHour) + (mMinute < 10 ? "0" + mMinute : mMinute) + (mAMPM == Calendar.AM ? "A" : "P");
    }
    /**
     * This method navigates and joins the session, checking that it exist
     */
    private void joinSession(String sessionName, BrowserUser usr) throws ElementNotFoundException {
        List<String> session_titles = SessionNavigationUtilities.getFullSessionList(usr.getDriver());
        assertTrue(session_titles.contains(sessionName), "Session has not been created");
        WebElement session = SessionNavigationUtilities.getSession(usr.getDriver(), sessionName);
        Click.element(usr.getDriver(), session.findElement(SESSION_LIST_SESSION_ACCESS));
    }
    /**
     * Makes each student driver navigate into the selected course and join the video session
     */
    private void joinSessionStudents(String sessionName) throws ElementNotFoundException {
        for (BrowserUser student : studentBrowserUserList) {
            navigateToCourse(student, courseName);
            joinSession(sessionName, student);
        }
    }
    /**
     * From all pages, go to course tab and navigates to the selected course
     */
    private void navigateToCourse(BrowserUser usr, String courseName) throws ElementNotFoundException {
        List<String> courses = CourseNavigationUtilities.getCoursesList(usr.getDriver());
        assertFalse(courses.isEmpty(), "No courses in the list");
        WebElement course = CourseNavigationUtilities.getCourseByName(usr.getDriver(), courseName);
        course.findElement(COURSE_LIST_COURSE_TITLE).click();
        Wait.notTooMuch(usr.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
        CourseNavigationUtilities.go2Tab(usr.getDriver(), SESSION_ICON);
    }
    /**
     * Makes all the students leave the video session and return to the course main page
     */
    private void leaveStudentSessions() throws ElementNotFoundException {
        for (BrowserUser student : studentBrowserUserList) {
            Wait.notTooMuch(student.getDriver());
            leaveSession(student);
        }
    }
    /**
     * Given a user leaves the video session and returns to the course main page
     */
    private void leaveSession(BrowserUser usr) throws ElementNotFoundException {
        Click.element(usr.getDriver(), SESSION_LEFT_MENU_BUTTON);
        WebElement exitButton = usr.getDriver().findElement(SESSION_EXIT_ICON);
        JavascriptExecutor executor = (JavascriptExecutor) usr.getDriver();
        executor.executeScript("arguments[0].click();", exitButton);
        Wait.waitForPageLoaded(usr.getDriver());//13 lines
        //Wait for something
        Wait.notTooMuch(usr.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(COURSE_TABS));
    }
    /**
     * Removes the video session specified by parameter
     * @param sessionName string with the session name
     */
    private void deleteSession(String sessionName) throws ElementNotFoundException {
        List<String> session_titles;
        WebElement modal;
        WebElement session;
        session = SessionNavigationUtilities.getSession(user.getDriver(), sessionName);
        Click.element(user.getDriver(), session.findElement(SESSION_LIST_SESSION_EDIT_ICON));
        modal = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(SESSION_LIST_EDIT_MODAL));
        Click.element(user.getDriver(), modal.findElement(SESSION_LIST_EDIT_MODAL_DELETE_DIV).findElement(By.tagName("label")));
        Click.element(user.getDriver(), modal.findElement(SESSION_LIST_EDIT_MODAL_DELETE_DIV).findElement(By.tagName("a")));
        Wait.waitForPageLoaded(user.getDriver());//13 lines
        session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver()); //7 lines
        assertFalse(session_titles.contains(sessionName), "Session has not been deleted");
    }

    public String loadStudentsData(String path) throws IOException {
        FileReader file;
        StringBuilder key = new StringBuilder();
        file = new FileReader(path);
        BufferedReader reader = new BufferedReader(file);
        String line = reader.readLine();
        while (line != null) {
            key.append(line);
            line = reader.readLine();
        }
        System.out.println(key);
        return key.toString();
    }

}
