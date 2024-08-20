package com.fullteaching.e2e.no_elastest.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DOMManager {

    public static WebElement getParent(WebDriver wd, WebElement childElement) {
        JavascriptExecutor executor = (JavascriptExecutor) wd;
        return (WebElement) executor.executeScript("return arguments[0].parentNode;", childElement);
    }
}