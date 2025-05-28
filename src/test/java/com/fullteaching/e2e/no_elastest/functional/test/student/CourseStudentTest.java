package com.fullteaching.e2e.no_elastest.functional.test.student;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import giis.retorch.annotations.AccessMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static org.junit.jupiter.api.Assertions.fail;

class CourseStudentTest extends BaseLoggedTest {

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestStudents();
    }

    /**
     * These tests get the login the user as student, go the courses  and check if
     * there is any course in the list.After it, click in the first course of the list
     * and wait for the visibility of it.In second place, the student go to the home,
     * Session,Forum, Files and attenders tab to check if they are visible.
     */
    @AccessMode(resID = "course", concurrency = 15, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "openvidumock", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    @DisplayName("studentCourseMainTest")
    @ParameterizedTest
    @MethodSource("data")
    void studentCourseMainTest(String userMail, String password, String role) throws ElementNotFoundException, InterruptedException, NotLoggedException {//45+ 107+28 set up +13 lines teardown =193
            this.slowLogin(user, userMail, password);//24 lines
            NavigationUtilities.toCoursesHome(driver); //3lines
            //go to first course
            //get course list
            List<String> course_list = CourseNavigationUtilities.getCoursesList(driver); //13 lines
            if (course_list.isEmpty()) fail("No courses available for test user");
            WebElement course_button = CourseNavigationUtilities.getCourseByName(driver, course_list.get(0)).findElement(By.className("title")); //14 lines
            Click.element(driver, course_button);
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(COURSE_TABS_TAG)));
            CourseNavigationUtilities.go2Tab(driver, HOME_ICON);//4 lines
            CourseNavigationUtilities.go2Tab(driver, SESSION_ICON);//4lines
            CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);//4lines
            CourseNavigationUtilities.go2Tab(driver, FILES_ICON);//4lines
            CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON);//4lines
    }
}