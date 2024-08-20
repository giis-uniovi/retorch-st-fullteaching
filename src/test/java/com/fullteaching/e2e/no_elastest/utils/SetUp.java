package com.fullteaching.e2e.no_elastest.utils;

import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.fullteaching.e2e.no_elastest.common.Constants.LOCALHOST;
import static java.lang.System.getProperty;
import static java.lang.invoke.MethodHandles.lookup;
import static org.openqa.selenium.OutputType.BASE64;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.slf4j.LoggerFactory.getLogger;

public class SetUp {

    private static final Logger log = LoggerFactory.getLogger(SetUp.class);

    public static void tearDown(WebDriver driver) throws InterruptedException {
        if (driver != null) {
            log.info("url:" + driver.getCurrentUrl() + "\nScreenshot (in Base64) at the end of the test:\n{}",
                    getBase64Screenshot(driver));

            log.info("Browser console at the end of the test");
            LogEntries logEntries = driver.manage().logs().get(BROWSER);
            logEntries.forEach((entry) -> log.info("[{}] {} {}",
                    new Date(entry.getTimestamp()), entry.getLevel(),
                    entry.getMessage()));

                TimeUnit.SECONDS.sleep(300);
        }
    }

    public static String getBase64Screenshot(WebDriver driver) {
        log.debug("getBase64Screenshot INI");
        String screenshotBase64 = ((TakesScreenshot) driver)
                .getScreenshotAs(BASE64);

        log.debug("getBase64Screenshot END");

        return "data:image/png;base64," + screenshotBase64;
    }

    public static String cleanEmptyCourse(WebDriver driver) throws ElementNotFoundException {
        String course_title = "Test Course_" + System.currentTimeMillis();

        return CourseNavigationUtilities.newCourse(driver, course_title);
    }
}