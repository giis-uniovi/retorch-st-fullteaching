package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;


public class ForumNavigationUtilities {
    private static final Logger log = LoggerFactory.getLogger(ForumNavigationUtilities.class);

    public static boolean isForumEnabled(WebElement forumTabContent) { //6lines
        log.info("Checking if the forum is enabled");
        try {
            log.info("Forum enabled, looking for NEW ENTRY ICON");
            forumTabContent.findElement(FORUM_NEW_ENTRY_ICON);
            return true;
        } catch (Exception e) {
            log.info("Forum Disabled");
            return false;
        }
    }


    public static List<String> getFullEntryList(WebDriver wd) { //6 lines
        ArrayList<String> entries_titles = new ArrayList<>();
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(FORUM_ICON));
        WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, FORUM_ICON);
        List<WebElement> entries = tab_content.findElements(By.className("entry-title"));
        for (WebElement entry : entries) {
            entries_titles.add(entry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE).getText());
        }
        return entries_titles;
    }

    public static List<String> getUserEntries(WebDriver wd, String user_name) {
        ArrayList<String> entries_titles = new ArrayList<>();

        WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, FORUM_ICON);
        List<WebElement> entries = tab_content.findElements(By.className("entry-title"));
        for (WebElement entry : entries) {
            //if username is the publisher of the entry...
            entries_titles.add(entry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE).getText());
        }

        return entries_titles;
    }

    public static WebElement getEntry(WebDriver wd, String entry_name) throws ElementNotFoundException { //16 lines
        log.info("Getting the entry with title {}", entry_name);

        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_ICON));
        WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, FORUM_ICON);
        //Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.className("entry-title")));
        List<WebElement> entries = tab_content.findElements(By.className("entry-title"));
        for (WebElement entry : entries) {
            try {
                WebElement title = entry.findElement(FORUM_ENTRY_LIST_ENTRY_TITLE);
                String title_text = title.getText();
                if (title_text == null || title_text.equals("")) {
                    title_text = title.getAttribute("innerHTML");
                }
                if (entry_name.equals(title_text)) {

                    return entry;
                }
            } catch (NoSuchElementException csee) {
                //do nothing and look for the next item
            }
        }
        throw new ElementNotFoundException(String.format("[getEntry] The entry with title \"%s\" the entry doesn't exist, the number of entries was %s", entry_name, entries.size()));
    }

    public static List<WebElement> getComments(WebDriver wd) {
        log.info("Getting entry comments");
        Wait.notTooMuch(wd).until(ExpectedConditions.numberOfElementsToBeMoreThan(FORUM_COMMENT_LIST_COMMENT, 0));
        return wd.findElements(FORUM_COMMENT_LIST_COMMENT);
    }

    public static List<WebElement> getUserComments(WebDriver wd, String user_name) {//8lines
        List<WebElement> user_comments = new ArrayList<>();
        Wait.notTooMuch(wd).until(ExpectedConditions.numberOfElementsToBeMoreThan(FORUM_COMMENT_LIST_COMMENT, 0));
        List<WebElement> all_comments = wd.findElements(FORUM_COMMENT_LIST_COMMENT);
        log.info("Getting the comments of the user: {} ", user_name);
        for (WebElement comment : all_comments) {
            String comment_username = comment.findElement(FORUM_COMMENT_LIST_COMMENT_USER).getText();
            if (user_name.equals(comment_username)) {
                user_comments.add(comment);
            }
        }
        return user_comments;
    }

    public static List<WebElement> getHighLightedComments(WebDriver wd, String user_name) {
        List<WebElement> user_comments = new ArrayList<>();

        List<WebElement> all_comments = wd.findElements(FORUM_COMMENT_LIST_COMMENT);

        for (WebElement comment : all_comments) {
            String comment_username = comment.findElement(FORUM_COMMENT_LIST_COMMENT_USER).getText();
            if (user_name.equals(comment_username)) {
                user_comments.add(comment);
            }
        }
        return user_comments;
    }

    public static WebDriver newEntry(WebDriver wd, String newEntryTitle, String newEntryContent) throws ElementNotFoundException { //16 lines
        log.info("Creating a new entry");
        CourseNavigationUtilities.go2Tab(wd, FORUM_ICON);
        assertTrue(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(wd, FORUM_ICON)), "Forum not activated");
        Click.element(wd, FORUM_NEW_ENTRY_ICON);
        //wait for modal
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEW_ENTRY_MODAL));
        //fill new Entry
        WebElement title = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEW_ENTRY_MODAL_TITLE));
        log.info("Setting the title: {}", title);
        title.sendKeys(newEntryTitle);
        WebElement comment = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEW_ENTRY_MODAL_CONTENT));
        log.info("Setting the title: {}", newEntryContent);
        comment.sendKeys(newEntryContent);
        //Publish
        log.info("Click the publish button");
        Click.element(wd, FORUM_NEW_ENTRY_MODAL_POST_BUTTON);
        //Wait to publish
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_ENTRY_LIST_ENTRIES_UL));
        //Check entry...
        Wait.waitForPageLoaded(wd);
        ForumNavigationUtilities.getEntry(wd, newEntryTitle);
        return wd;
    }


    public static List<WebElement> getReplies(WebDriver driver, WebElement comment) { //7 lines
        log.info("Get all the replies of the selected comment");
        List<WebElement> replies = new ArrayList<>();
        //get all comment-div
        List<WebElement> nestedComments = comment.findElements(FORUM_COMMENT_LIST_COMMENT_DIV);
        //ignore first it is original comment
        for (int i = 1; i < nestedComments.size(); i++) {
            replies.add(nestedComments.get(i));
        }
        return replies;
    }

    public static WebDriver enableForum(WebDriver wd) throws ElementNotFoundException { //11lines
        log.info("Checking that the forum is enable, click into the edit button");
        //click edit
        WebElement edit_button = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_EDIT_ENTRY_ICON));
        Click.element(wd, edit_button);
        WebElement edit_modal = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(ENABLE_FORUM_MODAL));
        //press enable
        log.info("Click the enable button");
        Wait.waitForPageLoaded(wd);
        WebElement enable_button = edit_modal.findElement(ENABLE_FORUM_BUTTON);
        Wait.waitForPageLoaded(wd);
        Click.element(wd, enable_button);
        Wait.waitForPageLoaded(wd);
        WebElement save_button = edit_modal.findElement(ENABLE_FORUM_MODAL_SAVE_BUTTON);
        Wait.waitForPageLoaded(wd);
        log.info("Click save button");
        Click.element(wd, save_button);
        Wait.waitForPageLoaded(wd);
        WebElement forum_tab_content = CourseNavigationUtilities.wait4TabContent(wd, FORUM_ICON);
        Wait.waitForPageLoaded(wd);
        log.info("Checking that the forum es enabled");
        assertTrue(isForumEnabled(forum_tab_content), "The forum is not disabled");
        return wd;
    }

    public static WebDriver disableForum(WebDriver wd) throws ElementNotFoundException { //12 lines
        //click edit
        log.info("Checking that the forum is disabled, click into the edit button");
        WebElement edit_button = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(FORUM_EDIT_ENTRY_ICON));
        Click.element(wd, edit_button);
        WebElement edit_modal = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(ENABLE_FORUM_MODAL));
        log.info("Click into the disable button");
        //press disable
        WebElement enable_button = edit_modal.findElement(DISABLE_FORUM_BUTTON);
        Click.element(wd, enable_button);
        WebElement save_button = edit_modal.findElement(ENABLE_FORUM_MODAL_SAVE_BUTTON);
        log.info("Click into the save button");
        Click.element(wd, save_button);
        WebElement forum_tab_content = CourseNavigationUtilities.wait4TabContent(wd, FORUM_ICON);
        Wait.waitForPageLoaded(wd);
        log.info("Finally checks that the Forum is enabled");
        assertFalse(ForumNavigationUtilities.isForumEnabled(forum_tab_content), "The forum is not disabled");
        return wd;
    }

}
