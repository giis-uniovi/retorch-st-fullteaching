/*
 * (C) Copyright 2017 OpenVidu (http://openvidu.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.fullteaching.e2e.no_elastest.functional.test.media;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * E2E tests for FullTeaching REST CRUD operations.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */
//@Disabled
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching REST CRUD operations")
class FullTeachingEndToEndRESTTests extends BaseLoggedTest {


    final String TEST_COURSE_INFO = "TEST_COURSE_INFO";
    final String EDITED = " EDITED";
    final String TEACHER_NAME = "Teacher Cheater";
    String COURSE_NAME = "TEST_COURSE";

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }

    public FullTeachingEndToEndRESTTests() {
        super();
    }

    /*** ClassRule methods ***/

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Configuration"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("courseRestOperations")
    @ParameterizedTest
    @MethodSource("data")
    void courseRestOperations(String mail, String password, String role ) throws ElementNotFoundException {
        loginAndCreateNewCourse(mail,password);

        editCourse();

        CourseNavigationUtilities.deleteCourse(teacher.getDriver(), COURSE_NAME + EDITED); // Tear down
    }

    private void loginAndCreateNewCourse(String mail, String password) throws ElementNotFoundException {
        slowLogin(teacher, mail, password);
        CourseNavigationUtilities.newCourse(teacher.getDriver(), COURSE_NAME);
    }

    private void editCourse() {
        log.info("Editing course");
        String editedCourseName = COURSE_NAME + EDITED;
        List<WebElement> editIcons = teacher.getDriver().findElements(By.className("course-put-icon"));
        openDialog(editIcons.get(editIcons.size() - 1), teacher);
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.id("input-put-course-name")), "Input for course name not clickable");
        WebElement courseNameInput = teacher.getDriver().findElement(By.id("input-put-course-name"));
        courseNameInput.clear();
        courseNameInput.sendKeys(editedCourseName);
        teacher.getDriver().findElement(By.id("submit-put-course-btn")).click();
        waitForDialogClosed("course-modal", "Edition of course failed", teacher);
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), editedCourseName), "Unexpected course name");
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Information"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("courseInfoRestOperations")
    @ParameterizedTest
    @MethodSource("data")
    void courseInfoRestOperations(String mail, String password, String role) throws ElementNotFoundException { //12+16+65 set up +60 lines teardown =153
        loginAndCreateNewCourse(mail,password);
        enterCourseAndNavigateTab(COURSE_NAME, "info-tab-icon");//16 lines
        teacher.waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(By.cssSelector(".md-tab-body.md-tab-active"), By.cssSelector(".card-panel.warning")), "Course info wasn't empty");
        log.info("Editing course information");

        teacher.getDriver().findElement(By.id("edit-course-info")).click();
        teacher.getDriver().findElement(By.className("ql-editor")).sendKeys(TEST_COURSE_INFO);
        teacher.getDriver().findElement(By.id("send-info-btn")).click();

        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".ql-editor p"), TEST_COURSE_INFO), "Unexpected course info");
        log.info("Course information successfully updated");
        CourseNavigationUtilities.deleteCourse(teacher.getDriver(), COURSE_NAME);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("sessionRestOperations")
    @ParameterizedTest
    @MethodSource("data")
    void sessionRestOperations(String mail, String password, String role) throws ElementNotFoundException {
        loginAndCreateNewCourse(mail,password);

        addNewSession();

        editSession();

        deleteSession();

        CourseNavigationUtilities.deleteCourse(teacher.getDriver(), COURSE_NAME);
    }

    private void addNewSession() {
        enterCourseAndNavigateTab(COURSE_NAME, "sessions-tab-icon");
        log.info("Adding new session");
        openDialog("#add-session-icon", teacher);
        fillSessionForm("TEST LESSON NAME", "TEST LESSON COMMENT", LocalDate.parse("2018-03-01"), LocalTime.parse("15:10"), false);
        waitForDialogClosed("course-details-modal", "Addition of session failed", teacher);
        verifySessionDetails("TEST LESSON NAME", "TEST LESSON COMMENT", "Jan 3, 2018 - 03:10", "Mar 1, 2018 - 15:10");
    }

    private void editSession() {
        log.info("Editing session");
        openDialog(".edit-session-icon", teacher);
        fillSessionForm("TEST LESSON NAME EDITED", "TEST LESSON COMMENT EDITED", LocalDate.parse("2019-04-02"), LocalTime.parse("05:10"), true);
        waitForDialogClosed("put-delete-modal", "Edition of session failed", teacher);
        verifySessionDetails("TEST LESSON NAME EDITED", "TEST LESSON COMMENT EDITED", "Feb 4, 2019 - 05:10", "Apr 2, 2019 - 05:10");
    }

    private void deleteSession() {
        log.info("Deleting session");
        openDialog(".edit-session-icon", teacher);
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("label-delete-checkbox"))), "Checkbox for session deletion not clickable");
        teacher.getDriver().findElement(By.id("label-delete-checkbox")).click();
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("delete-session-btn"))), "Button for session deletion not clickable");
        teacher.getDriver().findElement(By.id("delete-session-btn")).click();
        waitForDialogClosed("put-delete-modal", "Deletion of session failed", teacher);
        teacher.waitUntil(ExpectedConditions.numberOfElementsToBe(By.cssSelector("li.session-data"), 0), "Unexpected number of sessions");
        log.info("Session successfully deleted");
    }

    private void fillSessionForm(String title, String comment, LocalDate date, LocalTime hour, boolean edit) {
        // Get the different fields depending on if it's editing the Session or not
        WebElement titleField = teacher.getDriver().findElement(By.id(edit ? "input-put-title" : "input-post-title"));
        WebElement commentField = teacher.getDriver().findElement(By.id(edit ? "input-put-comment" : "input-post-comment"));
        WebElement dateField = teacher.getDriver().findElement(By.id(edit ? "input-put-date" : "input-post-date"));
        WebElement timeField = teacher.getDriver().findElement(By.id(edit ? "input-put-time" : "input-post-time"));
        if (edit) { //If its editing previously clear the fields content
            titleField.clear();
            commentField.clear();
        }
        titleField.sendKeys(title);
        commentField.sendKeys(comment);
        // Create the correct date depending on the browser and submitting into teh form
        DateTimeFormatter dateFormatter = BROWSER_NAME.equals("chrome") ? DateTimeFormatter.ofPattern("MM/dd/yyyy") : DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timeFormatter = BROWSER_NAME.equals("chrome") ? DateTimeFormatter.ofPattern("hh:mma") : DateTimeFormatter.ofPattern("HH:mm");
        dateField.sendKeys(date.format(dateFormatter));
        timeField.sendKeys(hour.format(timeFormatter));
        // Submit session form
        teacher.getDriver().findElement(By.id(edit ? "put-modal-btn" : "post-modal-btn")).click();
    }

    private void verifySessionDetails(String expectedTitle, String expectedComment, String expectedDateTime1, String expectedDateTime2) {
        teacher.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-title")), "Unexpected session title");
        Assertions.assertEquals(expectedTitle, teacher.getDriver().findElement(By.cssSelector("li.session-data .session-title")).getText());

        teacher.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-description")), "The element located by css li.session-data .session-description is not visible");
        Assertions.assertEquals(expectedComment, teacher.getDriver().findElement(By.cssSelector("li.session-data .session-description")).getText());

        teacher.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-datetime")), "The element located by css li.session-data .session-datetime is not visible");
        String actualDateTime = teacher.getDriver().findElement(By.cssSelector("li.session-data .session-datetime")).getText();
        Assertions.assertTrue(actualDateTime.equals(expectedDateTime1) || actualDateTime.equals(expectedDateTime2));
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("forumRestOperations")
    @ParameterizedTest
    @MethodSource("data")
    void forumRestOperations(String mail, String password, String role) throws ElementNotFoundException { //60+66+65 set up +60 lines teardown =251
        loginAndCreateNewCourse(mail,password);
        enterCourseAndNavigateTab(COURSE_NAME, "forum-tab-icon");//16 lines
        log.info("Adding new entry to the forum");
        openDialog("#add-entry-icon", teacher);//8lines
        // Find form elements
        WebElement titleField = teacher.getDriver().findElement(By.id("input-post-title"));
        WebElement commentField = teacher.getDriver().findElement(By.id("input-post-comment"));
        String title = "TEST FORUM ENTRY";
        String comment = "TEST FORUM COMMENT";
        String entryDate = "a few seconds ago";
        // Fill input fields
        titleField.sendKeys(title);
        commentField.sendKeys(comment);
        teacher.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of entry failed", teacher);////14 lines
        // Check fields of new entry
        WebElement entryEl = teacher.getDriver().findElement(By.cssSelector("li.entry-title"));
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-title"), title),
                "Unexpected entry title in the forum");
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-author"), TEACHER_NAME),
                "Unexpected entry author in the forum");
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-date"), entryDate),
                "Unexpected entry date in the forum");
        log.info("New entry successfully added to the forum");
        log.info("Entering the new entry");
        entryEl.click();
        teacher.waitUntil(ExpectedConditions.textToBe(
                By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .message-itself"),
                comment), "Unexpected entry title in the entry details view");
        teacher.waitUntil(ExpectedConditions.textToBe(
                By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .forum-comment-author"),
                TEACHER_NAME), "Unexpected entry author in the entry details view");
        // Comment reply
        log.info("Adding new replay to the entry's only comment");
        String reply = "TEST FORUM REPLY";
        openDialog(".replay-icon", teacher);//8lines
        commentField = teacher.getDriver().findElement(By.id("input-post-comment"));
        commentField.sendKeys(reply);
        teacher.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of entry reply failed", teacher);//14 lines
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector(
                        ".comment-block > app-comment:first-child > div.comment-div div.comment-div .message-itself"),
                reply), "Unexpected reply message in the entry details view");
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector(
                        ".comment-block > app-comment:first-child > div.comment-div div.comment-div .forum-comment-author"),
                TEACHER_NAME), "Unexpected reply author in the entry details view");
        log.info("Replay successfully added");
        // Forum deactivation
        teacher.getDriver().findElement(By.id("entries-sml-btn")).click();
        log.info("Deactivating forum");
        openDialog("#edit-forum-icon", teacher);//8lines
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("label-forum-checkbox"))),
                "Checkbox for forum deactivation not clickable");
        teacher.getDriver().findElement(By.id("label-forum-checkbox")).click();
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("put-modal-btn"))),
                "Button for forum deactivation not clickable");
        teacher.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Deactivation of forum failed", teacher);//14 lines
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (forum deactivated) missing");
        log.info("Forum successfully deactivated");
        CourseNavigationUtilities.deleteCourse(teacher.getDriver(), COURSE_NAME);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Files"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("filesRestOperations")
    @ParameterizedTest
    @MethodSource("data")
    void filesRestOperations(String mail, String password, String role) throws ElementNotFoundException {//88+112+65 set up +60 lines teardown =325
        loginAndCreateNewCourse(mail,password);
        enterCourseAndNavigateTab(COURSE_NAME, "files-tab-icon");//16 lines
        log.info("Checking that there are no files in the course");
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (course with no files) missing");
        log.info("Adding new file group");
        openDialog("#add-files-icon", teacher);//8lines
        String fileGroup = "TEST FILE GROUP";
        // Find form elements
        WebElement titleField = teacher.getDriver().findElement(By.id("input-post-title"));
        titleField.sendKeys(fileGroup);
        teacher.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of file group failed", teacher);//14 lines
        // Check fields of new file group
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".file-group-title h5"), fileGroup),
                "Unexpected file group name");
        log.info("File group successfully added");
        // Edit file group
        log.info("Editing file group");
        openDialog("#edit-filegroup-icon", teacher);//8lines
        // Find form elements
        titleField = teacher.getDriver().findElement(By.id("input-file-title"));
        titleField.clear();
        titleField.sendKeys(fileGroup + EDITED);
        teacher.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Edition of file group failed", teacher);//14 lines
        // Check fields of edited file group
        teacher.waitUntil(
                ExpectedConditions.textToBe(By.cssSelector("app-file-group .file-group-title h5"), fileGroup + EDITED),
                "Unexpected file group name");
        log.info("File group successfully edited");
        // Add file subgroup
        log.info("Adding new file sub-group");
        String fileSubGroup = "TEST FILE SUBGROUP";
        openDialog(".add-subgroup-btn", teacher);//8lines
        titleField = teacher.getDriver().findElement(By.id("input-post-title"));
        titleField.sendKeys(fileSubGroup);
        teacher.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of file sub-group failed", teacher);//14 lines
        // Check fields of new file subgroup
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .file-group-title h5"),
                fileSubGroup), "Unexpected file sub-group name");
        log.info("File sub-group successfully added");
        log.info("Adding new file to sub-group");
        openDialog("app-file-group app-file-group .add-file-btn", teacher);//8lines
        WebElement fileUploader = teacher.getDriver().findElement(By.className("input-file-uploader"));
        String fileName = "testFile.txt";
        log.info("Uploading file located on path '{}'",
                System.getProperty("user.dir") + "/src/test/resources/" + fileName);
        teacher.runJavascript("arguments[0].setAttribute('style', 'display:block')", fileUploader);
        teacher.waitUntil(
                ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//input[contains(@class, 'input-file-uploader') and contains(@style, 'display:block')]")),
                "Waiting for the input file to be displayed");
        fileUploader.sendKeys(System.getProperty("user.dir") + "/src/test/resources/" + fileName);
        teacher.getDriver().findElement(By.id("upload-all-btn")).click();
        // Wait for upload
        teacher.waitUntil(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'determinate') and contains(@style, 'width: 100')]")),
                "Upload process not completed. Progress bar not filled");
        teacher.waitUntil(ExpectedConditions.textToBe(By.xpath("//i[contains(@class, 'icon-status-upload')]"), "done"),
                "Upload process failed");
        log.info("File upload successful");
        // Close dialog
        teacher.getDriver().findElement(By.id("close-upload-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Upload of file failed", teacher);//14 lines
        // Check new uploaded file
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
                fileName), "Unexpected uploaded file name");
        log.info("File successfully added");
        // Edit file
        log.info("Editing file");
        openDialog("app-file-group app-file-group .edit-file-name-icon", teacher);//8lines
        titleField = teacher.getDriver().findElement(By.id("input-file-title"));
        titleField.clear();
        String editedFileName = "testFileEDITED.txt";
        titleField.sendKeys(editedFileName);
        teacher.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Edition of file failed", teacher);//14 lines
        // Check edited file name
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
                editedFileName), "Unexpected uploaded file name");
        log.info("File successfully edited");
        // Delete file group
        log.info("Deleting file-group");
        teacher.getDriver().findElement(By.cssSelector("app-file-group .delete-filegroup-icon")).click();
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (course with no files) missing");
        log.info("File group successfully deleted");
        CourseNavigationUtilities.deleteCourse(teacher.getDriver(), COURSE_NAME);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Attenders"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("attendersRestOperations")
    @ParameterizedTest
    @MethodSource("data")
    void attendersRestOperations(String mail, String password, String role) throws ElementNotFoundException {//42+32+65 set up +60 lines teardown =199
        loginAndCreateNewCourse(mail,password);
        enterCourseAndNavigateTab(COURSE_NAME, "attenders-tab-icon");//16 lines
        log.info("Checking that there is only one attender to the course");
        teacher.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");
        teacher.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".attender-row-div .attender-name-p"), TEACHER_NAME),
                "Unexpected name for the attender");
        // Add attender fail
        log.info("Adding attender (should FAIL)");
        openDialog("#add-attenders-icon", teacher);//8lines
        String attenderName = "studentFail@gmail.com";
        WebElement titleField = teacher.getDriver().findElement(By.id("input-attender-simple"));
        titleField.sendKeys(attenderName);
        teacher.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Addition of attender fail", teacher);//14 lines
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.fail")),
                "Error card (attender not added to the course) missing");
        teacher.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");
        teacher.getDriver().findElement(By.cssSelector("app-error-message .card-panel.fail .material-icons")).click();
        log.info("Attender addition successfully failed");
        // Add attender success
        log.info("Adding attender (should SUCCESS)");
        openDialog("#add-attenders-icon", teacher);//8lines
        attenderName = "student1@gmail.com";
        titleField = teacher.getDriver().findElement(By.id("input-attender-simple"));
        titleField.sendKeys(attenderName);
        teacher.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Addition of attender failed", teacher);//14 lines
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.correct")),
                "Success card (attender properly added to the course) missing");
        teacher.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 2),
                "Unexpected number of attenders for the course");
        teacher.getDriver().findElement(By.cssSelector("app-error-message .card-panel.correct .material-icons")).click();
        log.info("Attender addition successfully finished");
        // Remove attender
        log.info("Removing attender");
        teacher.getDriver().findElement(By.id("edit-attenders-icon")).click();
        teacher.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector(".del-attender-icon")),
                "Button for attender deletion not clickable");
        teacher.getDriver().findElement(By.cssSelector(".del-attender-icon")).click();
        teacher.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");
        log.info("Attender successfully removed");
        CourseNavigationUtilities.deleteCourse(teacher.getDriver(), COURSE_NAME);
    }

    /*** Auxiliary methods ***/


    private void enterCourseAndNavigateTab(String courseName, String tabId) { //16 lines
        log.info("Entering course {}", courseName);
        //All these tests always create a new course, so we wait for 3-courses in the main page (more than 2)
        teacher.waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#course-list .course-list-item div.course-title span"), 2), "The number of courses should be 3");
        List<WebElement> allCourses = teacher.getDriver()
                .findElements(By.cssSelector("#course-list .course-list-item div.course-title span"));
        WebElement courseSpan = null;
        for (WebElement c : allCourses) {
            if (c.getText().equals(courseName)) {
                courseSpan = c;
                break;
            }
        }

        assertNotNull(courseSpan, "The course with the name '" + courseName + "' could not be found. Total courses available: " + allCourses.size());
        courseSpan.click();
        teacher.waitUntil(ExpectedConditions.textToBe(By.id("main-course-title"), courseName), "Unexpected course title");
        log.info("Navigating to tab by clicking icon with id '{}'", tabId);
        teacher.getDriver().findElement(By.id(tabId)).click();

    }


}