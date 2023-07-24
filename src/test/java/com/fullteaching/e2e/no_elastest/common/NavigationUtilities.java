package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import java.util.List;

import static com.fullteaching.e2e.no_elastest.common.Constants.COURSES_BUTTON;
import static com.fullteaching.e2e.no_elastest.common.Constants.COURSES_DASHBOARD_TITLE;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;


public class NavigationUtilities {
    final static Logger log = getLogger(lookup().lookupClass());

    public static boolean amINotHere(WebDriver wd, String url) {//9lines
        log.info("Checking if the browser its in the URL: {}", url);
        String currentUrl = wd.getCurrentUrl().trim();
        log.debug("The current url is: {}", currentUrl);
        String compareUrl = url;
        if (currentUrl.endsWith("/") && !compareUrl.trim().endsWith("/")) {
            compareUrl = compareUrl.trim() + "/";
        }
        if (!currentUrl.endsWith("/") && compareUrl.trim().endsWith("/")) {

            compareUrl = compareUrl.substring(0, compareUrl.length() - 2);
        }

        return (!currentUrl.equals(compareUrl));
    }

    public static void getUrl(WebDriver wd, String url) {

        if (amINotHere(wd, url))
            log.info("Navigating to: {}", url);
        wd.get(url);

    }

    public static void getUrlAndWaitFooter(WebDriver wd, String url) { //3lines
        getUrl(wd, url);
        log.debug("Waiting for the page being loaded");
        Wait.waitForPageLoaded(wd);
        //   Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(FOOTER));
    }

    public static WebDriver toCoursesHome(WebDriver wd) throws ElementNotFoundException { //3lines
        log.debug("Waiting for the element COURSES BUTTON");
        Wait.aLittle(wd).until(ExpectedConditions.presenceOfElementLocated(COURSES_BUTTON));
        log.info("Click into the CoursesButton");
        Click.element(wd, COURSES_BUTTON);
        Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSES_DASHBOARD_TITLE));
        log.debug("Waiting for the element COURSE_DASHBOARD_TITLE");
        return wd;
    }

    public static WebElement getOption(List<WebElement> options, String find, FindOption type, String attribute) {
        log.info("Getting the option {} from attribute {}", find, attribute);
        for (WebElement option : options) {
            switch (type) {
                case CLASS:
                    if (find.equals(option.getAttribute("class")))
                        return option;
                    break;
                case TEXT:
                    if (find.equals(option.getText()))
                        return option;
                    break;
                case VALUE:
                    if (find.equals(option.getAttribute("value")))
                        return option;
                    break;
                default:
                    if (find.equals(option.getAttribute(attribute)))
                        return option;
                    break;
            }
        }
        return null;
    }


    public enum FindOption {
        CLASS, TEXT, VALUE, ATTRIBUTE
    }

}
