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
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.logging.Level.ALL;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class ChromeUser extends BrowserUser {
    ChromeOptions options = new ChromeOptions();

    public ChromeUser(int timeOfWaitInSeconds, String testName, String userIdentifier) {
        super(timeOfWaitInSeconds);
        log.info("Starting the configuration of the web browser");
        log.debug(String.format("The Test names are: %s", testName));

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        //Problems with the max attempt of retry, solved with : https://github.com/aerokube/selenoid/issues/1124 solved with --disable-gpu
        //Problems with flakiness due to screen resolution solved with --start-maximized
        String[] arguments = {"--no-sandbox", "--disable-dev-shm-usage", "--allow-elevated-browser", "--disable-gpu", "--start-maximized"};

        log.debug("Adding the arguments ({})", Arrays.toString(arguments));
        for (String argument : arguments
        ) {
            options.addArguments(argument);
        }

        options.setAcceptInsecureCerts(true);
        //This capability is to store the logs of the test case
        log.debug("Added Capabilities of acceptInsecureCerts and ignore alarms");

        if (System.getenv("SELENOID_PRESENT") == null) {
            log.info("Using the Local WebDriver ()");
            this.driver = new ChromeDriver(options);
        } else {
            try {

                Map<String, Object> selenoidOptions = new HashMap<>();
                log.info("Using the remote WebDriver (Selenoid)");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
                log.debug("Adding all the extra capabilities needed: {testName,enableVideo,enableVNC,name,enableLog,videoName,screenResolution}");

                selenoidOptions.put("testName", testName + "_" + userIdentifier + "_" + format.format(new Date()));
                //CAPABILITIES FOR SELENOID RETORCH
                selenoidOptions.put("enableVideo", true);
                selenoidOptions.put("enableVNC", true);
                selenoidOptions.put("name", testName + "-" + userIdentifier);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");
                LocalDateTime now = LocalDateTime.now();
                String logName = System.getProperty("tjob_name") + "-" + dtf.format(now) + "-" + testName + "-" + userIdentifier + ".log";
                String videoName = System.getProperty("tjob_name") + "-" + dtf.format(now) + "-" + testName + "-" + userIdentifier + ".mp4";
                log.debug("The data of this test would be stored into: video name {} and the log is {}", videoName, logName);

                selenoidOptions.put("enableLog", true);
                selenoidOptions.put("logName ", logName);
                selenoidOptions.put("videoName", videoName);

                selenoidOptions.put("screenResolution", "1920x1080x24");

                options.setCapability("selenoid:options", selenoidOptions);

                //END CAPABILITIES FOR SELENOID RETORCH
                log.debug("Configuring the remote WebDriver ");
                RemoteWebDriver remote = new RemoteWebDriver((new URI("http://selenoid:4444/wd/hub")).toURL(), options);
                log.debug("Configuring the Local File Detector");
                remote.setFileDetector(new LocalFileDetector());
                this.driver = remote;
            } catch (MalformedURLException | URISyntaxException e) {
                throw new RuntimeException("Exception creating eusApiURL", e);
            }
        }
        log.debug("Configure the driver connection timeouts at ({})", this.timeOfWaitInSeconds);
        new WebDriverWait(driver, Duration.ofSeconds(this.timeOfWaitInSeconds));

        log.info("Driver Successfully configured");
        this.configureDriver();
    }

}