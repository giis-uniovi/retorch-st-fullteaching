package com.fullteaching.e2e.no_elastest.functional.test;

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
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static org.junit.jupiter.api.Assertions.*;


@Tag("e2e")
@DisplayName("E2E tests for FullTeaching Login Session")
class LoggedForumTest extends BaseLoggedTest {
    protected final String courseName = "Pseudoscientific course for treating the evil eye";
    protected final String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

    public LoggedForumTest() {
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
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 10, sharing = true, accessMode = "READONLY")
    @DisplayName("studentCourseMainTest")
    @ParameterizedTest
    @MethodSource("data")
    void forumLoadEntriesTest(String mail, String password, String role) { //47lines +115 +28 set up +13 lines teardown =203
        this.slowLogin(user, mail, password);//24 lines
        try {
            //navigate to courses.
            NavigationUtilities.toCoursesHome(driver);//3lines
            List<String> courses = CourseNavigationUtilities.getCoursesList(driver);//13lines
            assertFalse(courses.isEmpty(), "No courses in the list");
            //find course with forum activated
            boolean activated_forum_on_some_test = false;
            boolean has_comments = false;
            for (String course_name : courses) {
                //go to each of the courses
                WebElement course = CourseNavigationUtilities.getCourseByName(driver, course_name);//14lines
                course.findElement(COURSE_LIST_COURSE_TITLE).click();
                Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
                log.info("Navigating to the forum and checking if its enabled");
                //go to forum tab to check if enabled:
                //load forum
                CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);//4lines
                if (ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver, FORUM_ICON))) {
                    activated_forum_on_some_test = true;
                    log.info("Loading the entries list");
                    //Load list of entries
                    List<String> entries_list = ForumNavigationUtilities.getFullEntryList(driver);//6lines
                    if (!entries_list.isEmpty()) {
                        //Go into first entry
                        for (String entry_name : entries_list) {
                            log.info("Checking the entry with name: {}", entry_name);
                            WebElement entry = ForumNavigationUtilities.getEntry(driver, entry_name);//16lines
                            Click.element(driver, entry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE));
                            //Load comments
                            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST));
                            List<WebElement> comments = ForumNavigationUtilities.getComments(driver);
                            log.info("Checking if the entry has comments");
                            if (!comments.isEmpty()) {
                                has_comments = true;
                                log.info("Comments found, saving them");

                                ForumNavigationUtilities.getUserComments(driver, userName);//8lines
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

    /**
     * This test get login and create a custom title and content with the current date.
     * After that, navigate to course for access the forum section.In the forum creates
     * a new entry with the previous created title and content. Secondly, we ensure that
     * the entry was created correctly and ensures that there are only one comment that
     * corresponds with the body of that entry.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("studentCourseMainTest")
    @ParameterizedTest
    @MethodSource("data")
    void forumNewEntryTest(String mail, String password, String role) {// 48+ 104 +   28 set up +13 lines teardown =193
        this.slowLogin(user, mail, password); //24 lines
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);
        log.info("Setting new entry title and content");
        String newEntryTitle = "New Entry Test " + mDay + mMonth + mYear + mHour + mMinute + mSecond;
        String newEntryContent = "This is the content written on the " + mDay + " of " + months[mMonth] + ", " + mHour + ":" + mMinute + "," + mSecond;
        log.info("Navigating to courses tab");
        try {
            log.info("Navigating to courses tab");
            //navigate to courses.
            if (NavigationUtilities.amINotHere(driver, COURSES_URL.replace("__HOST__", HOST))) {
                NavigationUtilities.toCoursesHome(driver);//3lines
            }
            WebElement course = CourseNavigationUtilities.getCourseByName(driver, courseName);//14lines
            log.info("Entering the course List");
            course.findElement(COURSE_LIST_COURSE_TITLE).click();

            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
            log.info("Entering the Forum");
            CourseNavigationUtilities.go2Tab(driver, FORUM_ICON); //4lines
            assertTrue(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver, FORUM_ICON)), "Forum not activated"); //6lines
            Wait.waitForPageLoaded(driver);
            ForumNavigationUtilities.newEntry(driver, newEntryTitle, newEntryContent); //16lines
            //Retorch Modification, this test fails due to the speed of the browser
            Wait.waitForPageLoaded(driver);
            //Check entry... Flake Here
            WebElement newEntry = ForumNavigationUtilities.getEntry(driver, newEntryTitle);//16lines
            Wait.waitForPageLoaded(driver);
            assertEquals(newEntry.findElement(FORUM_ENTRY_LIST_ENTRY_USER).getText(), userName, "Incorrect user");
            Click.element(driver, newEntry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE));
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST));
            WebElement entryTitleRow = driver.findElement(FORUM_COMMENT_LIST_ENTRY_TITLE);
            assertEquals(entryTitleRow.getText().split("\n")[0], newEntryTitle, "Incorrect Entry Title");
            assertEquals(entryTitleRow.findElement(FORUM_COMMENT_LIST_ENTRY_USER).getText(), userName, "Incorrect User for Entry");
            //first comment should be the inserted while creating the entry
            Wait.waitForPageLoaded(driver);
            List<WebElement> comments = ForumNavigationUtilities.getComments(driver);
            assertFalse(comments.isEmpty(), "No comments on the entry");
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST));
            Wait.waitForPageLoaded(driver);
            WebElement newComment = comments.get(0);
            String commentContent = newComment.findElement(FORUM_COMMENT_LIST_COMMENT_CONTENT).getText();
            assertEquals(commentContent, newEntryContent, "Bad content of comment");
            Wait.waitForPageLoaded(driver);
            String comment = newComment.findElement(FORUM_COMMENT_LIST_COMMENT_USER).getText();
            assertEquals(comment, userName, "Bad user in comment");
        } catch (ElementNotFoundException notFoundException) {
            Assertions.fail("Failed to navigate to course forum:: " + notFoundException.getClass() + ": " + notFoundException.getLocalizedMessage());
        }
        //Fix Flaky test Navigating to the mainpage to logout...
        user.getDriver().get(APP_URL);
    }

    /**
     * This test get login and create a custom title and content with the current date.
     * After that, navigate to course for access the forum section.If in the forum
     * there are not any entries create a new entry and gets into it.In the other hand
     * if there are  any created previously entry get into the first of them. Secondly,
     * once we are into the entry, we look for the new comment button, making a new comment
     * in this entry with the custom content(the current date and hour).Finally, we iterate
     * over all comments looking for the comment that previously we create.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("forumNewCommentTest")
    @ParameterizedTest
    @MethodSource("data")
    void forumNewCommentTest(String mail, String password, String role) { // 69+142 + 28 set up +13 lines teardown =252
        this.slowLogin(user, mail, password); //24 lines
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);

        try {
            //check if one course have any entry for comment
            NavigationUtilities.toCoursesHome(driver);//3lines

            WebElement course = CourseNavigationUtilities.getCourseByName(driver, courseName);//14 lines
            course.findElement(COURSE_LIST_COURSE_TITLE).click();
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
            CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);//4lines
            assertTrue(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver, FORUM_ICON)), "Forum not activated");//6lines
            List<String> entries_list = ForumNavigationUtilities.getFullEntryList(driver);//6lines
            WebElement entry;
            if (entries_list.size() <= 0) {//if not new entry
                String newEntryTitle = "New Comment Test " + mDay + mMonth + mYear + mHour + mMinute + mSecond;
                String newEntryContent = "This is the content written on the " + mDay + " of " + months[mMonth - 1] + ", " + mHour + ":" + mMinute + "," + mSecond;
                ForumNavigationUtilities.newEntry(driver, newEntryTitle, newEntryContent);//16lines
                entry = ForumNavigationUtilities.getEntry(driver, newEntryTitle);//16lines
            } else {
                entry = ForumNavigationUtilities.getEntry(driver, entries_list.get(0));//16lines
            }
            //go to entry
            Click.element(driver, entry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE));
            WebElement commentList = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST));
            //counting the number of numberComments
            int numberCommentsOld = ForumNavigationUtilities.getComments(driver).size();
            //new comment
            WebElement newCommentIcon = commentList.findElement(FORUM_COMMENT_LIST_NEW_COMMENT_ICON);
            Click.element(driver, newCommentIcon);
            Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEW_COMMENT_MODAL));
            String newCommentContent = "COMMENT TEST" + mDay + mMonth + mYear + mHour + mMinute + mSecond + ". This is the comment written on the " + mDay + " of " + months[mMonth] + ", " + mHour + ":" + mMinute + "," + mSecond;
            WebElement comment_field = driver.findElement(FORUM_NEW_COMMENT_MODAL_TEXT_FIELD);
            comment_field.sendKeys(newCommentContent);
            Click.element(driver, FORUM_NEW_COMMENT_MODAL_POST_BUTTON);

            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST));
            //TO-DO think in other better way to solve this problem
            Wait.waitForPageLoaded(driver);
            user.waitUntil(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST_COMMENT), "The comment list are not visible");
            user.waitUntil((ExpectedCondition<Boolean>) driver -> {
                int elementCount = ForumNavigationUtilities.getComments(driver).size();
                return elementCount > numberCommentsOld;
            }, "Comment not attached");
            List<WebElement> comments = ForumNavigationUtilities.getComments(driver);
            //asserts
            assertTrue(comments.size() > numberCommentsOld, "Comment list empty or only original comment");

            user.waitUntil(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST_COMMENT), "The comment list are not visible");

            boolean commentFound = false;
            for (WebElement comment : comments) {
                //check if it is new comment
                try {
                    String text = comment.findElement(FORUM_COMMENT_LIST_COMMENT_CONTENT).getText();
                    if (text.equals(newCommentContent)) {
                        commentFound = true;
                        assertEquals(comment.findElement(FORUM_COMMENT_LIST_COMMENT_USER).getText(), userName, "Bad user in comment");
                    }
                } catch (StaleElementReferenceException e) {
                    log.info("Not Found");
                }
            }
            assertTrue(commentFound, "Comment not found");
        } catch (ElementNotFoundException notFoundException) {
            fail("Failed to navigate to course forum:: " + notFoundException.getClass() + ": " + notFoundException.getLocalizedMessage());
        }

    }

    /**
     * This test get login and create like the previously a custom content to make a comment
     * We proceed navigate to the courses' forum zone, and check if there are any entries.
     * In the case that there are not entries, create a new entry and  replies to the
     * first comment of it ( the content of it).In the other hand if there are entries
     * previously created, go to the first and replies to the same comment.After it, we check
     * that the comment was correctly published.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("forumNewReply2CommentTest")
    @ParameterizedTest
    @MethodSource("data")
    void forumNewReply2CommentTest(String mail, String password, String role) { // 63+137+ 28 set up +13 lines teardown = 242
        this.slowLogin(user, mail, password);//24 lines
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);
        String newEntryTitle;
        try {
            //check if one course have any entry for comment
            NavigationUtilities.toCoursesHome(driver);//3lines
            WebElement course = CourseNavigationUtilities.getCourseByName(driver, courseName);//14 lines
            course.findElement(COURSE_LIST_COURSE_TITLE).click();
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
            CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);//4 lines
            assertTrue(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver, FORUM_ICON)), "Forum not activated");//2lines
            List<String> entries_list = ForumNavigationUtilities.getFullEntryList(driver);//6lines
            WebElement entry;
            if (entries_list.isEmpty()) {//if not new entry
                newEntryTitle = "New Comment Test " + mDay + mMonth + mYear + mHour + mMinute + mSecond;
                String newEntryContent = "This is the content written on the " + mDay + " of " + months[mMonth - 1] + ", " + mHour + ":" + mMinute + "," + mSecond;
                ForumNavigationUtilities.newEntry(driver, newEntryTitle, newEntryContent); //19 lines
                entry = ForumNavigationUtilities.getEntry(driver, newEntryTitle); //16 lines
            } else {
                entry = ForumNavigationUtilities.getEntry(driver, entries_list.get(0)); //16 lines
            }
            //go to entry
            Click.element(driver, entry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE));
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST));
            List<WebElement> comments = ForumNavigationUtilities.getComments(driver);//2lines
            //go to first comment
            WebElement comment = comments.get(0);
            Click.element(driver, comment.findElement(FORUM_COMMENT_LIST_COMMENT_REPLY_ICON));
            String newReplyContent = "This is the reply written on the " + mDay + " of " + months[mMonth] + ", " + mHour + ":" + mMinute + "," + mSecond;
            //reply
            Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST_MODAL_NEW_REPLY));
            WebElement textField = driver.findElement(FORUM_COMMENT_LIST_MODAL_NEW_REPLY_TEXT_FIELD);
            textField.sendKeys(newReplyContent);
            Click.element(user.getDriver(), FORUM_NEW_COMMENT_MODAL_POST_BUTTON);
            user.waitUntil(ExpectedConditions.invisibilityOfElementLocated(FORUM_COMMENT_LIST_MODAL_NEW_REPLY),"The model is still visible");
            user.waitUntil(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST), "The comments are not visible");

            user.waitUntil(ExpectedConditions.visibilityOfElementLocated(FORUM_COMMENT_LIST_COMMENT), "The comment list are not visible");
            comments = ForumNavigationUtilities.getComments(user.getDriver()); //2lines
            //getComment replies
            List<WebElement> replies = ForumNavigationUtilities.getReplies(user.getDriver(), comments.get(0)); // 7 lines
            WebElement newReply = null;
            for (WebElement reply : replies) {
                String text = reply.getText();
                if (text.contains(newReplyContent))
                    newReply = reply;
            }
            //assert reply
            assertNotNull(newReply, "Reply not found");
            boolean isNameEqual = newReply.findElement(FORUM_COMMENT_LIST_COMMENT_USER).getText().equals(userName);
            assertTrue(isNameEqual, "Bad user in comment");
            //nested reply
            //assert nested reply
        } catch (ElementNotFoundException notFoundException) {
            fail("Failed to navigate to course forum:: " + notFoundException.getClass() + ": " + notFoundException.getLocalizedMessage());
        }
    }

}
