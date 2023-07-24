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
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * E2E tests for FullTeaching REST CRUD operations.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */
//@Disabled
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching REST CRUD operations")
@ExtendWith(SeleniumJupiter.class)
class FullTeachingEndToEndRESTTests extends BaseLoggedTest {


    final String TEST_COURSE_INFO = "TEST_COURSE_INFO";
    final String EDITED = " EDITED";
    final String TEACHER_MAIL = "teacher@gmail.com";
    final String TEACHER_PASS = "pass";
    final String TEACHER_NAME = "Teacher Cheater";
    String COURSE_NAME = "TEST_COURSE";

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
    @Test
    void courseRestOperations() throws ElementNotFoundException { //14+22+65 set up +60 lines teardown =161
        // Edit course
        this.slowLogin(user, TEACHER_MAIL, TEACHER_PASS);
        // Teacher login//51lines
        CourseNavigationUtilities.newCourse(user.getDriver(), HOST, COURSE_NAME); // Add test course //14 lines
        log.info("Editing course");
        COURSE_NAME = COURSE_NAME + EDITED;
        List<WebElement> l = user.getDriver().findElements(By.className("course-put-icon"));
        openDialog(l.get(l.size() - 1), user);//8lines
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("input-put-course-name"))),
                "Input for course name not clickable");
        user.getDriver().findElement(By.id("input-put-course-name")).clear();
        user.getDriver().findElement(By.id("input-put-course-name")).sendKeys(COURSE_NAME);
        user.getDriver().findElement(By.id("submit-put-course-btn")).click();
        waitForDialogClosed("course-modal", "Edition of course failed", user);//14lines
        user.waitUntil(
                ExpectedConditions.textToBe(
                        By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), COURSE_NAME),
                "Unexpected course name");

        CourseNavigationUtilities.deleteCourse(user.getDriver(), COURSE_NAME, HOST);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Information"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void courseInfoRestOperations() throws ElementNotFoundException { //12+16+65 set up +60 lines teardown =153
        // Empty course info
        this.slowLogin(user, TEACHER_MAIL, TEACHER_PASS);
        // Teacher login//51lines
        CourseNavigationUtilities.newCourse(user.getDriver(), HOST, COURSE_NAME); // Add test course //14 lines
        enterCourseAndNavigateTab(COURSE_NAME, "info-tab-icon");//16 lines
        user.waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(By.cssSelector(".md-tab-body.md-tab-active"),
                By.cssSelector(".card-panel.warning")), "Course info wasn't empty");
        log.info("Editing course information");
        // Edit course info
        user.getDriver().findElement(By.id("edit-course-info")).click();
        user.getDriver().findElement(By.className("ql-editor")).sendKeys(TEST_COURSE_INFO);
        user.getDriver().findElement(By.id("send-info-btn")).click();


        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".ql-editor p"), TEST_COURSE_INFO),
                "Unexpected course info");
        log.info("Course information successfully updated");
        CourseNavigationUtilities.deleteCourse(user.getDriver(), COURSE_NAME, HOST);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void sessionRestOperations() throws ElementNotFoundException { //77+66+65 set up +60 lines teardown = 268
        // Add new session
        this.slowLogin(user, TEACHER_MAIL, TEACHER_PASS);
        // Teacher login//51lines
        CourseNavigationUtilities.newCourse(user.getDriver(), HOST, COURSE_NAME); // Add test course //14 lines

        enterCourseAndNavigateTab(COURSE_NAME, "sessions-tab-icon");//16 lines
        log.info("Adding new session");
        openDialog("#add-session-icon", user);//8lines
        // Find form elements
        WebElement titleField = user.getDriver().findElement(By.id("input-post-title"));
        WebElement commentField = user.getDriver().findElement(By.id("input-post-comment"));
        WebElement dateField = user.getDriver().findElement(By.id("input-post-date"));
        WebElement timeField = user.getDriver().findElement(By.id("input-post-time"));
        String title = "TEST LESSON NAME";
        String comment = "TEST LESSON COMMENT";
        // Fill input fields
        titleField.sendKeys(title);
        commentField.sendKeys(comment);
        if (BROWSER_NAME.equals("chrome")) {
            dateField.sendKeys("03/01/2018");
            timeField.sendKeys("03:10PM");
        } else if (BROWSER_NAME.equals("firefox")) {
            dateField.sendKeys("2018/03/01");
            timeField.sendKeys("15:10");
        }
        user.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of session failed", user);//14 lines
        // Check fields of added session
        //Changed, this is more adequate for check de error
        user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-title")),
                "Unexpected session title");
        Assertions.assertEquals(user.getDriver().findElement(By.cssSelector("li.session-data .session-title")).getText(), title);


        user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-description")), "The element located by css li.session-data .session-description is not visible");
        Assertions.assertEquals(user.getDriver().findElement(By.cssSelector("li.session-data .session-description")).getText(), comment);


        user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-datetime")), "The element located by css li.session-data .session-datetime is not visible");
        Assertions.assertTrue(user.getDriver().findElement(By.cssSelector("li.session-data .session-datetime")).getText().equals("Jan 3, 2018 - 03:10") || user.getDriver().findElement(By.cssSelector("li.session-data .session-datetime")).getText().equals("Mar 1, 2018 - 15:10"));


        log.info("New session successfully added");
        // Edit session
        log.info("Editing session");
        openDialog(".edit-session-icon", user);//8lines
        // Find form elements
        titleField = user.getDriver().findElement(By.id("input-put-title"));
        commentField = user.getDriver().findElement(By.id("input-put-comment"));
        dateField = user.getDriver().findElement(By.id("input-put-date"));
        timeField = user.getDriver().findElement(By.id("input-put-time"));
        // Clear elements
        titleField.clear();
        commentField.clear();
        // Fill edited input fields
        titleField.sendKeys(title + EDITED);
        commentField.sendKeys(comment + EDITED);
        if (BROWSER_NAME.equals("chrome")) {
            dateField.sendKeys("04/02/2019");
            timeField.sendKeys("05:10AM");
        } else if (BROWSER_NAME.equals("firefox")) {
            dateField.sendKeys("2019-04-02");
            timeField.sendKeys("05:10");
        }
        user.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Edition of session failed", user);//14 lines
        // Check fields of edited session
        log.info("Checking title of the edited session");
        user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-title")), "The element located by css li.session-data .session-title is not visible");
        Assertions.assertEquals(title + EDITED, user.getDriver().findElement(By.cssSelector("li.session-data .session-title")).getText());

        user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-description")), "The element located by css \"li.session-data .session-description\" is not visible");
        Assertions.assertEquals(comment + EDITED, user.getDriver().findElement(By.cssSelector("li.session-data .session-description")).getText());

        user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.session-data .session-datetime")), "The element located by css \"li.session-data .session-datetime\" is not visible");
        Assertions.assertTrue(user.getDriver().findElement(By.cssSelector("li.session-data .session-datetime")).getText().equals("Feb 4, 2019 - 05:10") || user.getDriver().findElement(By.cssSelector("li.session-data .session-datetime")).getText().equals("Apr 2, 2019 - 05:10"));

        log.info("Session successfully edited");
        // Delete session
        log.info("Deleting session");
        openDialog(".edit-session-icon", user);//8lines
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("label-delete-checkbox"))),
                "Checkbox for session deletion not clickable");
        user.getDriver().findElement(By.id("label-delete-checkbox")).click();
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("delete-session-btn"))),
                "Button for session deletion not clickable");
        user.getDriver().findElement(By.id("delete-session-btn")).click();
        waitForDialogClosed("put-delete-modal", "Deletion of session failed", user);//14 lines
        user.waitUntil(ExpectedConditions.numberOfElementsToBe(By.cssSelector("li.session-data"), 0),
                "Unexpected number of sessions");
        log.info("Session successfully deleted");
        CourseNavigationUtilities.deleteCourse(user.getDriver(), COURSE_NAME, HOST);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void forumRestOperations() throws ElementNotFoundException { //60+66+65 set up +60 lines teardown =251
        // Add new entry to the forum
        this.slowLogin(user, TEACHER_MAIL, TEACHER_PASS);
        // Teacher login//51lines
        CourseNavigationUtilities.newCourse(user.getDriver(), HOST, COURSE_NAME); // Add test course //14 lines
        enterCourseAndNavigateTab(COURSE_NAME, "forum-tab-icon");//16 lines
        log.info("Adding new entry to the forum");
        openDialog("#add-entry-icon", user);//8lines
        // Find form elements
        WebElement titleField = user.getDriver().findElement(By.id("input-post-title"));
        WebElement commentField = user.getDriver().findElement(By.id("input-post-comment"));
        String title = "TEST FORUM ENTRY";
        String comment = "TEST FORUM COMMENT";
        String entryDate = "a few seconds ago";
        // Fill input fields
        titleField.sendKeys(title);
        commentField.sendKeys(comment);
        user.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of entry failed", user);////14 lines
        // Check fields of new entry
        WebElement entryEl = user.getDriver().findElement(By.cssSelector("li.entry-title"));
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-title"), title),
                "Unexpected entry title in the forum");
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-author"), TEACHER_NAME),
                "Unexpected entry author in the forum");
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-date"), entryDate),
                "Unexpected entry date in the forum");
        log.info("New entry successfully added to the forum");
        log.info("Entering the new entry");
        entryEl.click();
        user.waitUntil(ExpectedConditions.textToBe(
                By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .message-itself"),
                comment), "Unexpected entry title in the entry details view");
        user.waitUntil(ExpectedConditions.textToBe(
                By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .forum-comment-author"),
                TEACHER_NAME), "Unexpected entry author in the entry details view");
        // Comment reply
        log.info("Adding new replay to the entry's only comment");
        String reply = "TEST FORUM REPLY";
        openDialog(".replay-icon", user);//8lines
        commentField = user.getDriver().findElement(By.id("input-post-comment"));
        commentField.sendKeys(reply);
        user.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of entry reply failed", user);//14 lines
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector(
                        ".comment-block > app-comment:first-child > div.comment-div div.comment-div .message-itself"),
                reply), "Unexpected reply message in the entry details view");
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector(
                        ".comment-block > app-comment:first-child > div.comment-div div.comment-div .forum-comment-author"),
                TEACHER_NAME), "Unexpected reply author in the entry details view");
        log.info("Replay successfully added");
        // Forum deactivation
        user.getDriver().findElement(By.id("entries-sml-btn")).click();
        log.info("Deactivating forum");
        openDialog("#edit-forum-icon", user);//8lines
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("label-forum-checkbox"))),
                "Checkbox for forum deactivation not clickable");
        user.getDriver().findElement(By.id("label-forum-checkbox")).click();
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("put-modal-btn"))),
                "Button for forum deactivation not clickable");
        user.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Deactivation of forum failed", user);//14 lines
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (forum deactivated) missing");
        log.info("Forum successfully deactivated");
        CourseNavigationUtilities.deleteCourse(user.getDriver(), COURSE_NAME, HOST);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Files"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void filesRestOperations() throws ElementNotFoundException {//88+112+65 set up +60 lines teardown =325
        this.slowLogin(user, TEACHER_MAIL, TEACHER_PASS);
        // Teacher login//51lines
        CourseNavigationUtilities.newCourse(user.getDriver(), HOST, COURSE_NAME); // Add test course //14 lines
        enterCourseAndNavigateTab(COURSE_NAME, "files-tab-icon");//16 lines
        log.info("Checking that there are no files in the course");
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (course with no files) missing");
        log.info("Adding new file group");
        openDialog("#add-files-icon", user);//8lines
        String fileGroup = "TEST FILE GROUP";
        // Find form elements
        WebElement titleField = user.getDriver().findElement(By.id("input-post-title"));
        titleField.sendKeys(fileGroup);
        user.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of file group failed", user);//14 lines
        // Check fields of new file group
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".file-group-title h5"), fileGroup),
                "Unexpected file group name");
        log.info("File group successfully added");
        // Edit file group
        log.info("Editing file group");
        openDialog("#edit-filegroup-icon", user);//8lines
        // Find form elements
        titleField = user.getDriver().findElement(By.id("input-file-title"));
        titleField.clear();
        titleField.sendKeys(fileGroup + EDITED);
        user.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Edition of file group failed", user);//14 lines
        // Check fields of edited file group
        user.waitUntil(
                ExpectedConditions.textToBe(By.cssSelector("app-file-group .file-group-title h5"), fileGroup + EDITED),
                "Unexpected file group name");
        log.info("File group successfully edited");
        // Add file subgroup
        log.info("Adding new file sub-group");
        String fileSubGroup = "TEST FILE SUBGROUP";
        openDialog(".add-subgroup-btn", user);//8lines
        titleField = user.getDriver().findElement(By.id("input-post-title"));
        titleField.sendKeys(fileSubGroup);
        user.getDriver().findElement(By.id("post-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Addition of file sub-group failed", user);//14 lines
        // Check fields of new file subgroup
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .file-group-title h5"),
                fileSubGroup), "Unexpected file sub-group name");
        log.info("File sub-group successfully added");
        log.info("Adding new file to sub-group");
        openDialog("app-file-group app-file-group .add-file-btn", user);//8lines
        WebElement fileUploader = user.getDriver().findElement(By.className("input-file-uploader"));
        String fileName = "testFile.txt";
        log.info("Uploading file located on path '{}'",
                System.getProperty("user.dir") + "/src/test/resources/" + fileName);
        user.runJavascript("arguments[0].setAttribute('style', 'display:block')", fileUploader);
        user.waitUntil(
                ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//input[contains(@class, 'input-file-uploader') and contains(@style, 'display:block')]")),
                "Waiting for the input file to be displayed");
        fileUploader.sendKeys(System.getProperty("user.dir") + "/src/test/resources/" + fileName);
        user.getDriver().findElement(By.id("upload-all-btn")).click();
        // Wait for upload
        user.waitUntil(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'determinate') and contains(@style, 'width: 100')]")),
                "Upload process not completed. Progress bar not filled");
        user.waitUntil(ExpectedConditions.textToBe(By.xpath("//i[contains(@class, 'icon-status-upload')]"), "done"),
                "Upload process failed");
        log.info("File upload successful");
        // Close dialog
        user.getDriver().findElement(By.id("close-upload-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Upload of file failed", user);//14 lines
        // Check new uploaded file
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
                fileName), "Unexpected uploaded file name");
        log.info("File successfully added");
        // Edit file
        log.info("Editing file");
        openDialog("app-file-group app-file-group .edit-file-name-icon", user);//8lines
        titleField = user.getDriver().findElement(By.id("input-file-title"));
        titleField.clear();
        String editedFileName = "testFileEDITED.txt";
        titleField.sendKeys(editedFileName);
        user.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Edition of file failed", user);//14 lines
        // Check edited file name
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
                editedFileName), "Unexpected uploaded file name");
        log.info("File successfully edited");
        // Delete file group
        log.info("Deleting file-group");
        user.getDriver().findElement(By.cssSelector("app-file-group .delete-filegroup-icon")).click();
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (course with no files) missing");
        log.info("File group successfully deleted");
        CourseNavigationUtilities.deleteCourse(user.getDriver(), COURSE_NAME, HOST);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Attenders"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void attendersRestOperations() throws ElementNotFoundException {//42+32+65 set up +60 lines teardown =199
        this.slowLogin(user, TEACHER_MAIL, TEACHER_PASS);
        // Teacher login//51lines
        CourseNavigationUtilities.newCourse(user.getDriver(), HOST, COURSE_NAME); // Add test course //14 lines
        enterCourseAndNavigateTab(COURSE_NAME, "attenders-tab-icon");//16 lines
        log.info("Checking that there is only one attender to the course");
        user.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");
        user.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".attender-row-div .attender-name-p"), TEACHER_NAME),
                "Unexpected name for the attender");
        // Add attender fail
        log.info("Adding attender (should FAIL)");
        openDialog("#add-attenders-icon", user);//8lines
        String attenderName = "studentFail@gmail.com";
        WebElement titleField = user.getDriver().findElement(By.id("input-attender-simple"));
        titleField.sendKeys(attenderName);
        user.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Addition of attender fail", user);//14 lines
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.fail")),
                "Error card (attender not added to the course) missing");
        user.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");
        user.getDriver().findElement(By.cssSelector("app-error-message .card-panel.fail .material-icons")).click();
        log.info("Attender addition successfully failed");
        // Add attender success
        log.info("Adding attender (should SUCCESS)");
        openDialog("#add-attenders-icon", user);//8lines
        attenderName = "student1@gmail.com";
        titleField = user.getDriver().findElement(By.id("input-attender-simple"));
        titleField.sendKeys(attenderName);
        user.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Addition of attender failed", user);//14 lines
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.correct")),
                "Success card (attender properly added to the course) missing");
        user.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 2),
                "Unexpected number of attenders for the course");
        user.getDriver().findElement(By.cssSelector("app-error-message .card-panel.correct .material-icons")).click();
        log.info("Attender addition successfully finished");
        // Remove attender
        log.info("Removing attender");
        user.getDriver().findElement(By.id("edit-attenders-icon")).click();
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector(".del-attender-icon")),
                "Button for attender deletion not clickable");
        user.getDriver().findElement(By.cssSelector(".del-attender-icon")).click();
        user.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");
        log.info("Attender successfully removed");
        CourseNavigationUtilities.deleteCourse(user.getDriver(), COURSE_NAME, HOST);
    }

    /*** Auxiliary methods ***/


    private void enterCourseAndNavigateTab(String courseName, String tabId) { //16 lines
        log.info("Entering course {}", courseName);
        List<WebElement> allCourses = user.getDriver()
                .findElements(By.cssSelector("#course-list .course-list-item div.course-title span"));
        WebElement courseSpan = null;
        for (WebElement c : allCourses) {
            if (c.getText().equals(courseName)) {
                courseSpan = c;
                break;
            }
        }
        assert courseSpan != null;
        courseSpan.click();
        user.waitUntil(ExpectedConditions.textToBe(By.id("main-course-title"), courseName), "Unexpected course title");
        log.info("Navigating to tab by clicking icon with id '{}'", tabId);
        user.getDriver().findElement(By.id(tabId)).click();

    }


}