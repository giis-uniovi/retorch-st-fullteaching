package com.fullteaching.e2e.no_elastest.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.fullteaching.e2e.no_elastest.common.Constants.FOOTER;

public class Wait {

    public static WebDriverWait notTooMuch(WebDriver wd) {
        return new WebDriverWait(wd, Duration.ofSeconds(20));
    }

    public static WebDriverWait aLittle(WebDriver wd) {
        return new WebDriverWait(wd, Duration.ofSeconds(4));
    }

    public static void footer(WebDriver wd) {
        notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(FOOTER));
    }

    public static void waitForPageLoaded(WebDriver driver) { //13 lines
        ExpectedCondition<Boolean> expectation = driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").toString().equals("complete");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(expectation);
    }

}