package com.fullteaching.e2e.no_elastest.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class Scroll {
    public static void toElement(WebDriver wd, WebElement ele) {
        Actions actions = new Actions(wd);
        actions.moveToElement(ele);
        actions.perform();
    }
}