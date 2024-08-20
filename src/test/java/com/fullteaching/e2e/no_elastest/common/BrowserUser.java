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
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BrowserUser {

    public static final Logger log = LoggerFactory.getLogger(BrowserUser.class);
    protected final String clientData;
    protected final int timeOfWaitInSeconds;
    protected WebDriver driver;
    protected boolean isOnSession;
    protected WebDriverWait waiter;

    public BrowserUser(String clientData, int timeOfWaitInSeconds,String testName) {
        log.debug("Creating BrowserUser for the test: {}", testName);
        this.clientData = clientData;
        this.timeOfWaitInSeconds = timeOfWaitInSeconds;
        this.isOnSession = false;
    }

    public void configureRemoteWebDriver(String testName,MutableCapabilities options) throws URISyntaxException, MalformedURLException {
        log.debug("SELENOID_PRESENT, using the remote WebDriver (Selenoid)");
        Map<String, Object> selenoidOptions = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String baseName = System.getProperty("tjob_name") + "-" + dtf.format(now) + "-" + testName + "-" + clientData ;
        log.debug("Video and log files stored into .mp4 and .log named: {}" , baseName);
        log.debug("Adding all the extra capabilities needed: {testName,enableVideo,enableVNC,name,enableLog,videoName,screenResolution}");
        //CAPABILITIES FOR SELENOID
        selenoidOptions.put("testName", testName + "-" + clientData + "-" + dtf.format(now));
        selenoidOptions.put("enableVideo", true);
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("name", testName + "-" + clientData);
        selenoidOptions.put("enableLog", true);
        selenoidOptions.put("logName ", String.format("%s%s",baseName.replaceAll("\\s","") , ".log"));
        selenoidOptions.put("videoName", String.format("%s%s",baseName.replaceAll("\\s","") ,".mp4"));
        selenoidOptions.put("screenResolution", "1920x1080x24");
        options.setCapability("selenoid:options", selenoidOptions);
        //END CAPABILITIES FOR SELENOID RETORCH
        log.debug("Configuring the remote WebDriver ");
        RemoteWebDriver remote = new RemoteWebDriver(new URI("http://selenoid:4444/wd/hub").toURL(), options);
        log.debug("Configuring the Local File Detector");
        remote.setFileDetector(new LocalFileDetector());
        this.driver = remote;
        log.debug("End configuration of the RemoteWebDriver");
    }
    public void waitAndLastConfDriver(){
        log.debug("Configure the driver connection timeouts at ({})", this.timeOfWaitInSeconds);
        new WebDriverWait(driver, Duration.ofSeconds(this.timeOfWaitInSeconds));
        log.info("Driver Successfully configured");
        this.configureDriver();
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