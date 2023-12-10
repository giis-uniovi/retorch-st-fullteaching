/*
 * (C) Copyright 2017 OpenVidu (http://openvidu.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.fullteaching.e2e.no_elastest.common;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class BrowserUser {

    public static final Logger log = LoggerFactory.getLogger(BrowserUser.class);
    protected final String clientData;
    protected final int timeOfWaitInSeconds;
    protected WebDriver driver;
    protected boolean isOnSession;
    protected WebDriverWait waiter;

    public BrowserUser(String clientData, int timeOfWaitInSeconds) {
        this.clientData = clientData;
        this.timeOfWaitInSeconds = timeOfWaitInSeconds;
        this.isOnSession = false;
    }

    public WebDriver getDriver() {
        return this.driver;
    }


    public WebDriverWait getWaiter() {
        return this.waiter;
    }

    public void waitUntil(ExpectedCondition<?> condition, String errorMessage) {
        try {

            this.waiter.until(condition);
        } catch (org.openqa.selenium.TimeoutException timeout) {
            log.error(errorMessage);
            throw new org.openqa.selenium.TimeoutException("\"" + errorMessage + "\" (checked with condition) > " + timeout.getMessage());
        }
    }

    public String getClientData() {
        return this.clientData;
    }

    protected void configureDriver() {
        this.waiter = new WebDriverWait(this.driver, Duration.ofSeconds(this.timeOfWaitInSeconds));
    }

    public void dispose() {
        this.driver.quit();
    }

    public Object runJavascript(String script, Object... args) {
        return ((JavascriptExecutor) this.driver).executeScript(script, args);
    }

    public boolean isOnSession() {
        return this.isOnSession;
    }

    public void setOnSession(boolean onSession) {
        isOnSession = onSession;
    }
}
