package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;


public class UserUtilities {


    private static final Logger log = LoggerFactory.getLogger(UserUtilities.class);


    public static WebDriver checkLogin(WebDriver wd, String user) throws NotLoggedException, BadUserException, ElementNotFoundException { //12 lines
        log.info("[INI]checkLogin");
        //Wait to settings button to be present
        try {
            WebElement settings_button = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_BUTTON));
            Click.element(wd, settings_button);
        } catch (TimeoutException toe) {
            throw new NotLoggedException(toe.getMessage());
        }
        WebElement settings_page = Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_USER_EMAIL));
        //Check if the username is the expected
        if (!settings_page.getText().trim().equals(user.trim())) throw new BadUserException();
        log.info("[END]checkLogin");
        return wd;
    }


    public static void checkLogOut(WebDriver wd) throws ElementNotFoundException { //8lines
        log.info("[INI]checkLogOut");
        try {
            Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOGIN_MENU_XPATH)));
        } catch (TimeoutException toe) {
            throw new ElementNotFoundException("Not Logged Out. Not in the home");
        }
        log.info("[END]checkLogOut");

    }


}
