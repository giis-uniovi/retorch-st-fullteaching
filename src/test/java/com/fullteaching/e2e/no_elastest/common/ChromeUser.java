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

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static java.util.logging.Level.ALL;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class ChromeUser extends BrowserUser {

    ChromeOptions options = new ChromeOptions();

    public ChromeUser(String userIdentifier, int timeOfWaitInSeconds, String testName) throws URISyntaxException, MalformedURLException {
        super(userIdentifier, timeOfWaitInSeconds, testName);
        log.info("Starting the configuration of the Chrome web browser");

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        //Problems with the max attempt of retry, solved with : https://github.com/aerokube/selenoid/issues/1124 solved with --disable-gpu
        // options.addArguments("--disable-gpu"); Commented for the moment
        //Problems with flakiness due to screen resolution solved with --start-maximized
        options.addArguments("--start-maximized");
        options.setAcceptInsecureCerts(true);
        //This capability is to store the logs of the test case
        log.debug("Added Capabilities of acceptInsecureCerts and --start-maximized");

        if (System.getenv("SELENOID_PRESENT") == null) {
            log.info("Using the Local Chrome WebDriver ()");
            this.driver = new ChromeDriver(options);
        } else {
            configureRemoteWebDriver(testName, options);
        }
        waitAndLastConfDriver();
    }
}