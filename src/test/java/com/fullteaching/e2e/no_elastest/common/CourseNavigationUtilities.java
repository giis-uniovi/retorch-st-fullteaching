package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.DOMManager;
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
import java.util.concurrent.TimeUnit;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static org.slf4j.LoggerFactory.getLogger;

public class CourseNavigationUtilities {

    private static final Logger log = LoggerFactory.getLogger(CourseNavigationUtilities.class);

    public static String newCourse(WebDriver wd, String courseName) throws ElementNotFoundException { //37 lines
        NavigationUtilities.toCoursesHome(wd);

        log.debug("Checking existing courses...");
        Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(By.className("course-list-item")));
        WebElement newCourseButton = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(NEW_COURSE_BUTTON));
        Click.byJS(wd, newCourseButton);
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(NEW_COURSE_MODAL));

        log.debug("Introducing Course Name: "+courseName);
        WebElement nameField = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(NEW_COURSE_MODAL_NAME_FIELD));
        nameField.sendKeys(courseName);
        Click.element(wd, By.id(NEW_COURSE_MODAL_SAVE_ID));
        Wait.waitForPageLoaded(wd);

        checkIfCourseExists(wd, courseName);

        return courseName;
    }

    public static boolean checkIfCourseExists(WebDriver wd, String courseTitle) {
        WebElement courseList = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSE_LIST));

        List<WebElement> courses = courseList.findElements(By.tagName("li"));

        for (WebElement course : courses) {
            try {
                WebElement titleElement = course.findElement(By.className("title"));
                String titleText = titleElement.getText();

                if (courseTitle.equals(titleText)) {
                    log.info("The course with title {} exists!", courseTitle);
                    return true;
                }
            } catch (NoSuchElementException ignored) {
                // Do nothing and look for the next item
            }
        }
        return false;
    }

    public static boolean checkIfCourseExists(WebDriver wd, String course_title, int retries) { //10 lines
        for (int i = 0; i < retries; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error("The Sleeping could not be done");
            }
            if (checkIfCourseExists(wd, course_title)) {
                return true;
            }
        }
        return false;
    }

    public static WebDriver changeCourseName(WebDriver wd, String oldName, String newName) throws ElementNotFoundException {
        log.info("[INI] changeCourseName({}=>{})", oldName, newName);
        try {
            Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSE_LIST));
            log.info("Looking for the course");
            WebElement courseSelected = getCourseByName(wd, oldName);

            openEditCourseModal(wd, courseSelected);

            log.info("Changing the course Name");
            WebElement nameField = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_COURSE_MODAL_NAME_FIELD));
            nameField.clear();
            nameField.sendKeys(newName);

            saveChanges(wd);

        } catch (NoSuchElementException noSuchElementException) {
            log.info("[END] changeCourseName KO: Course \"{}\" probably doesn't exist", oldName);
            throw new ElementNotFoundException("changeCourseName - Course " + oldName + " probably doesn't exist");
        }

        log.info("[END] changeCourseName OK");
        return wd;
    }
    private static void openEditCourseModal(WebDriver wd, WebElement courseElement) throws ElementNotFoundException {
        WebElement editNameButton = courseElement.findElement(EDIT_COURSE_BUTTON);
        Click.element(wd, editNameButton);
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_DELETE_MODAL));
    }

    private static void saveChanges(WebDriver wd) throws ElementNotFoundException {
        log.debug("Click save button, saving changes...");
        Click.element(wd, EDIT_COURSE_MODAL_SAVE);
    }

    public static void deleteCourse(WebDriver wd, String courseName) throws ElementNotFoundException {
        log.info("[INI] deleteCourse({})", courseName);

        NavigationUtilities.toCoursesHome(wd);
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSE_LIST));

        try {
            WebElement courseElement = getCourseByName(wd, courseName);
            openEditCourseModal(wd, courseElement);

            log.info("Enabling delete course");
            WebElement deleteCheck = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_COURSE_DELETE_CHECK));
            Click.element(wd, deleteCheck);

            log.info("Click delete Course");
            WebElement deleteButton = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_COURSE_DELETE_BUTTON));
            Click.element(wd, deleteButton);

            saveChanges(wd);

        } catch (NoSuchElementException e) {
            log.error("[END] deleteCourse KO: Course \"{}\" probably doesn't exist", courseName);
            throw new ElementNotFoundException("deleteCourse - Course " + courseName + " probably doesn't exist");
        }

        log.info("[END] deleteCourse OK: Course \"{}\"", courseName);
    }

    public static List<String> getCoursesList(WebDriver wd) throws ElementNotFoundException {//13 lines
        ArrayList<String> courses_names = new ArrayList<>();
        NavigationUtilities.toCoursesHome(wd);
        WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSE_LIST));
        List<WebElement> courses = courses_list.findElements(By.tagName("li"));
        for (WebElement c : courses) {
            try {
                WebElement title = c.findElement(By.className("title"));
                String title_text = title.getText();
                courses_names.add(title_text);
            } catch (NoSuchElementException noSuchElementException) {
                //do nothing and look for the next item
            }
        }
        return courses_names;
    }

    public static WebElement getCourseByName(WebDriver wd, String name) throws ElementNotFoundException { //14 lines
        log.info("Finding the newly created course");
        WebElement coursesList = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSE_LIST));

        List<WebElement> courses = coursesList.findElements(By.tagName("li"));
        log.info("Iterating over the course lists");

        for (WebElement course : courses) {
            try {
                WebElement title = course.findElement(By.className("title"));
                String titleText = title.getText();
                if (name.equals(titleText)) {
                    log.info("Course with title {} found!", titleText);
                    return course;
                }
            } catch (NoSuchElementException noSuchElementException) {
                // Do nothing and look for the next item
            }
        }
        throw new ElementNotFoundException("getCourseElement - the course doesn't exist");
    }

    public static WebDriver go2Tab(WebDriver wd, By icon) throws ElementNotFoundException { //4lines
        WebElement tab = getTabElementFromIcon(wd, icon);
        String id = tab.getAttribute("id");
        log.info("Navigating to tab with id: {}", id);
        Click.element(wd, tab);
        id = id.replace("label", "content");
        Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        return wd;
    }

    public static String getTabId(WebDriver wd, By icon) {
        WebElement tab = getTabElementFromIcon(wd, icon);
        return tab.getAttribute("id");
    }

    public static WebElement getTabContent(WebDriver wd, By icon) {
        log.info("Get Tab content");
        String tab_id = getTabId(wd, icon);
        return wd.findElement(By.id(tab_id.replace("label", "content")));
    }

    public static WebElement wait4TabContent(WebDriver wd, By icon) {
        log.info("Waiting for tab content");
        String tab_id = getTabId(wd, icon);
        return Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.id(tab_id.replace("label", "content"))));
    }

    public static WebElement getTabElementFromIcon(WebDriver wd, By icon) {
        WebElement icon_element = wd.findElement(COURSE_TABS).findElement(icon);

        WebElement parent1 = DOMManager.getParent(wd, icon_element);

        WebElement parent2 = DOMManager.getParent(wd, parent1);
        DOMManager.getParent(wd, parent2);
        return parent2;
    }

    public static boolean isUserInAttendersList(WebDriver wd, String user_name) throws ElementNotFoundException {//15 lines
        log.info("[INI] isUserInAttendersList");
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(ATTENDERS_ICON));
        getTabContent(wd, ATTENDERS_ICON);
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(ATTENDERS_LIST_ROWS));
        List<WebElement> attenders_lst = wd.findElements(ATTENDERS_LIST_ROWS);
        if (attenders_lst.size() < 1) {
            log.info("[END] isUserInAttendersList KO: attenders list is empty");
            throw new ElementNotFoundException("isUserInAttendersList - attenders list is empty");
        }
        for (WebElement webElement : attenders_lst) {
            String user_from_row = webElement.getText();
            if (user_name.trim().equalsIgnoreCase(user_from_row.trim())) {
                log.info("[END] isUserInAttendersList OK");
                return true;
            }
        }
        log.info("[END] isUserInAttendersList KO: user not found");
        return false;
    }

    public static String getHighlightedAttender(WebDriver wd) throws ElementNotFoundException { //7lines
        log.info("[INI] getHighlightedAttender");
        WebElement attenders_content = getTabContent(wd, ATTENDERS_ICON);
        List<WebElement> attender_highlighted = attenders_content.findElements(ATTENDERS_LIST_HIGHLIGHTED_ROW);
        if (attender_highlighted == null || attender_highlighted.size() < 1) {
            log.info("[END] getHighlightedAttender KO: no highlighted user");
            throw new ElementNotFoundException("getHighlightedAttender - no highlighted user");
        }
        return attender_highlighted.get(0).getText();
    }


}