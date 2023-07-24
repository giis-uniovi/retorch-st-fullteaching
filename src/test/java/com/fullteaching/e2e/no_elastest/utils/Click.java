package com.fullteaching.e2e.no_elastest.utils;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class Click {

    public static final Logger log = getLogger(lookup().lookupClass());

    public static WebDriver here(WebDriver wd, int x, int y) {
        Actions builder = new Actions(wd);

        builder.moveToElement(wd.findElement(By.tagName("body")), x, y).click().build().perform();

        return wd;
    }

    public static WebDriver withNRetries(WebDriver wd, By eleBy, int n, By waitFor) throws ElementNotFoundException {
        /*properties for log*/
        String tagName = wd.findElement(eleBy).getTagName();
        String text = wd.findElement(eleBy).getText();

        int i = 0;

        try {
            Scroll.toElement(wd, wd.findElement(eleBy));
        } catch (Exception e) {
            log.error("Click.withNRetries: Failed on scroll");
        }
        do {
            try {
                Wait.notTooMuch(wd).until(ExpectedConditions.elementToBeClickable(wd.findElement(eleBy)));
                wd.findElement(eleBy).click();
                Wait.notTooMuch(wd).until(ExpectedConditions.visibilityOfElementLocated(waitFor));
                log.info("Click.withNRetries (click): ele:" + tagName + ":" + text + " ==>OK");
                return wd;
            } catch (Exception e) {
                try {
                    log.error("Click.withNRetries n (click):" + i + " " + e.getClass().getName() + ":" + e.getLocalizedMessage());
                    byJS(wd, wd.findElement(eleBy));
                    log.info("Click.element (ByJs): ele:" + tagName + ":" + text + " ==>OK");
                    return wd;
                } catch (Exception ex) {
                    log.error("Click.withNRetries n (ByJS):" + i + " " + ex.getClass().getName() + ":" + ex.getLocalizedMessage());
                    i++;
                }
            }

        } while (i < n);

        log.error("Click.withNRetries: ele:" + tagName + ":" + text + " ==>KO");
        throw new ElementNotFoundException("Click doesn't work properly");
    }

    public static void byJS(WebDriver wd, WebElement we) {
        JavascriptExecutor js = (JavascriptExecutor) wd;
        js.executeScript("var evt = document.createEvent('MouseEvents');"
                + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                + "arguments[0].dispatchEvent(evt);", we);
    }


    public static WebDriver element(WebDriver wd, WebElement ele) throws ElementNotFoundException {

        String tagName = ele.getTagName();
        String text = ele.getText();

        try {
            Scroll.toElement(wd, ele);
        } catch (Exception e) {
            log.error("Click.element: Scroll failed continuing...");
        }
        //try by click
        try {
            Wait.notTooMuch(wd).until(ExpectedConditions.elementToBeClickable(ele));
            ele.click();
            log.info("Click.element (click): ele:" + tagName + ":" + text + " ==>OK");
            return wd;
        } catch (Exception e) {
            log.error("Click.element (click): ele:" + tagName + ":" + text + " ==>KO " + e.getClass().getName() + ":" + e.getLocalizedMessage());
        }
        //Try by Js
        try {
            byJS(wd, ele);
            log.info("Click.element (ByJs): ele:" + tagName + ":" + text + " ==>OK");
            return wd;
        } catch (Exception e) {
            log.error("Click.element (ByJs): ele:" + tagName + ":" + text + " ==>KO " + e.getClass().getName() + ":" + e.getLocalizedMessage());
        }

        throw new ElementNotFoundException("Click.element ERROR");
    }

    /**
     * Scrolls and click
     *
     * @param wd    WebDriver of the method
     * @param eleBy Element to found
     * @throws ElementNotFoundException Exception if is not found the object
     */
    public static WebDriver element(WebDriver wd, By eleBy) throws ElementNotFoundException {

        WebElement ele = wd.findElement(eleBy);

        return element(wd, ele);

    }
}
