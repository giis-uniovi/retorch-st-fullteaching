package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.fullteaching.e2e.no_elastest.common.BaseLoggedTest.HOST;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;


public class NavigationUtilities {
    private static final Logger log = LoggerFactory.getLogger(NavigationUtilities.class);

    public static boolean amINotHere(WebDriver wd, String url) {
        log.info("Checking if the browser is in the URL: {}", url);
        String currentUrl = wd.getCurrentUrl().trim();
        log.debug("The current URL is: {}", currentUrl);
        String compareUrl = url.trim();

        if (currentUrl.endsWith("/") && !compareUrl.endsWith("/")) {
            compareUrl += "/";
        } else if (!currentUrl.endsWith("/") && compareUrl.endsWith("/")) {
            compareUrl = compareUrl.substring(0, compareUrl.length() - 1);
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
        if (NavigationUtilities.amINotHere(wd, COURSES_URL.replace("__HOST__", HOST))) {
            log.debug("Waiting for the element COURSES BUTTON");
            Wait.aLittle(wd).until(ExpectedConditions.presenceOfElementLocated(COURSES_BUTTON));
            log.info("Click into the CoursesButton");
            Click.element(wd, COURSES_BUTTON);
            Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSES_DASHBOARD_TITLE));
            log.debug("Waiting for the element COURSE_DASHBOARD_TITLE");
        }
        else{
            log.debug("The user was in the Course Tab");
        }
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
