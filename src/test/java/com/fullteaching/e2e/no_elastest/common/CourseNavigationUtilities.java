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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class CourseNavigationUtilities {

    public static final Logger log = getLogger(lookup().lookupClass());

    public static String newCourse(WebDriver wd, String host, String courseName) throws ElementNotFoundException { //37 lines
        boolean found = false;
        // navigate to course if not there
        if (NavigationUtilities.amINotHere(wd, COURSES_URL.replace("__HOST__", host))) {
            NavigationUtilities.toCoursesHome(wd);
        }
        List<WebElement> allCoursesPriorDeleting = wd.findElements(By.className("course-list-item"));
        // press new course button and wait for modal course-modal
        log.debug("Waiting for the presence of NEW COURSE BUTTON");
        WebElement new_course_button = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(NEW_COURSE_BUTTON));
        log.debug("Click by JS NEW COURSE BUTTON");
        Click.byJS(wd, new_course_button);
        log.debug("Waiting for the presence of NEW COURSE MODAL");
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(NEW_COURSE_MODAL));

        //fill information
        WebElement name_field = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(NEW_COURSE_MODAL_NAME_FIELD));

        log.debug("Sending COURSE TITLE");
        name_field.sendKeys(courseName); //no duplicated courses
        log.debug("Waiting for COURSE SAVE BUTTON");
        Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(NEW_COURSE_MODAL_SAVE));
        log.debug("Click SAVE  BUTTON");
        Click.element(wd, By.id(NEW_COURSE_MODAL_SAVE_ID));
        Wait.waitForPageLoaded(wd);


        //check if the course appears now in the list
        WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSE_LIST));
        Wait.waitForPageLoaded(wd);
        //find the newly create course
        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.tagName("li")));
        log.debug("Waiting for more than one course");
        Wait.notTooMuch(wd).until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("li"), allCoursesPriorDeleting.size()));
        if (!checkIfCourseExists(wd, courseName)) {
            log.error("newCourse - Course hasn't been created");
            throw new ElementNotFoundException("newCourse - Course hasn't been created");
        }
        return courseName;
    }

    public static boolean checkIfCourseExists(WebDriver wd, String course_title) {
        WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSE_LIST));
        //find the newly create course
        log.info("Finding the newly create course");
        List<WebElement> courses = courses_list.findElements(By.tagName("li"));
        log.info("Iterating over the courses list");
        for (WebElement c : courses) {
            try {
                WebElement title = c.findElement(By.className("title"));
                String title_text = title.getText();

                if (course_title.equals(title_text)) {
                    log.info("The course wit title {} exist!", course_title);
                    return true;
                }
            } catch (NoSuchElementException noSuchElementException) {
                //do nothing and look for the next item
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

    public static WebDriver changeCourseName(WebDriver wd, String oldName, String newName) throws ElementNotFoundException {//21lines
        log.info("[INI] changeCourseName({}=>{})", oldName, newName);

        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSE_LIST));
        try {
            //find the course
            log.info("Looking for the course");
            WebElement c = getCourseElement(wd, oldName);
            WebElement edit_name_button = c.findElement(EDIT_COURSE_BUTTON);
            Click.element(wd, edit_name_button);
            log.info("Waiting for edit modal");
            //wait for edit modal
            Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_DELETE_MODAL));
            //change name
            log.info("Changing the course Name");
            WebElement name_field = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_COURSE_MODAL_NAME_FIELD));
            name_field.clear();
            name_field.sendKeys(newName);
            log.info("Click save button");
            //save
            wd = Click.element(wd, EDIT_COURSE_MODAL_SAVE);
        } catch (NoSuchElementException noSuchElementException) {
            log.info("[END] changeCourseName KO: changeCourseName - Course \"{}\" probably doesn't exists", oldName);
            throw new ElementNotFoundException("changeCourseName - Course " + oldName + "probably doesn't exists");
        }
        log.info("[END] changeCourseName OK");
        return wd;
    }

    public static void deleteCourse(WebDriver wd, String course_name, String host) throws ElementNotFoundException {
        log.info("[INI] deleteCourse({})", course_name);
        if (NavigationUtilities.amINotHere(wd, COURSES_URL.replace("__HOST__", host))) {
            NavigationUtilities.toCoursesHome(wd);
        }

        Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(COURSE_LIST));

        try {

            //find the course

            WebElement c = getCourseElement(wd, course_name);

            WebElement edit_name_button = c.findElement(EDIT_COURSE_BUTTON);

            Click.element(wd, edit_name_button);

            //wait for edit modal
            Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_DELETE_MODAL));
            log.info("Enabling delete course");
            //Allow deleting
            WebElement delete_check = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_COURSE_DELETE_CHECK));
            Click.element(wd, delete_check);
            log.info("Click delete Course");
            //press delete
            WebElement delete_button = Wait.aLittle(wd).until(ExpectedConditions.visibilityOfElementLocated(EDIT_COURSE_DELETE_BUTTON));
            Click.element(wd, delete_button);
            log.info("Save changes");
            //save
            Click.element(wd, EDIT_COURSE_MODAL_SAVE);

        } catch (NoSuchElementException noSuchElementException) {
            log.info("[END] deleteCourse KO: changeCourseName - Course \"{}\" probably doesn't exists", course_name);
            throw new ElementNotFoundException("changeCourseName - Course " + course_name + "probably doesn't exists");
        }
        log.info("[END] deleteCourse OK: changeCourseName - Course \"{}\" ", course_name);

    }


    public static List<String> getCoursesList(WebDriver wd, String host) throws ElementNotFoundException {//13 lines
        ArrayList<String> courses_names = new ArrayList<>();
        if (NavigationUtilities.amINotHere(wd, COURSES_URL.replace("__HOST__", host))) {
            NavigationUtilities.toCoursesHome(wd);
        }
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
   /* public static WebDriver removeTestCourseIfExist(WebDriver wd, String host) throws ElementNotFoundException {//13 lines
        List<String > courses= getCoursesList(wd,  host);
        if courses.get(courses.s)

        return wd;

    }*/


    public static WebElement getCourseElement(WebDriver wd, String name) throws ElementNotFoundException { //14 lines
        log.info("Finding the newly create course");
        WebElement courses_list = Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSE_LIST));
        //find the newly create course
        List<WebElement> courses = courses_list.findElements(By.tagName("li"));
        log.info("Iterating over the course lists");
        for (WebElement c : courses) {
            try {
                WebElement title = c.findElement(By.className("title"));
                String title_text = title.getText();
                if (name.equals(title_text)) {
                    log.info("Course with title {} found!", title_text);
                    return c;
                }
            } catch (NoSuchElementException noSuchElementException) {
                //do nothing and look for the next item
            }
        }
        throw new ElementNotFoundException("getCourseElement-the course doesn't exist");
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