package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;

public class SessionNavigationUtilities {

    public static List<String> getFullSessionList(WebDriver wd) { //7lines
        ArrayList<String> session_titles = new ArrayList<>();
        WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, SESSION_ICON);
        List<WebElement> sessions = tab_content.findElements(SESSION_LIST_SESSION_ROW);
        for (WebElement session : sessions) {
            session_titles.add(session.findElement(SESSION_LIST_SESSION_NAME).getText());
        }
        return session_titles;
    }

    public static WebElement getSession(WebDriver wd, String session_name) throws ElementNotFoundException {//17 lines
        WebElement tab_content = CourseNavigationUtilities.getTabContent(wd, SESSION_ICON);
        List<WebElement> sessions = tab_content.findElements(SESSION_LIST_SESSION_ROW);
        for (WebElement session : sessions) {
            try {
                WebElement title = session.findElement(SESSION_LIST_SESSION_NAME);
                String title_text = title.getText();
                if (title_text == null || title_text.isEmpty()) {
                    title_text = title.getAttribute("innerHTML");
                }
                if (session_name.equals(title_text)) {
                    return session;
                }
            } catch (NoSuchElementException noSuchElementExcept) {
                //do nothing and look for the next item
            }
        }
        throw new ElementNotFoundException("getSession-the session doesn't exist");
    }
}
