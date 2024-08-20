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

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FirefoxUser extends BrowserUser {
    FirefoxOptions options = new FirefoxOptions();

    public FirefoxUser(String userIdentifier, int timeOfWaitInSeconds, String testName) throws URISyntaxException, MalformedURLException {
        super(userIdentifier, timeOfWaitInSeconds);
        //TO-DO Firefox configuration has changed, review it.
        FirefoxProfile profile = new FirefoxProfile();
        // This flag avoids granting the access to the camera
        profile.setPreference("media.navigator.permission.disabled", true);
        // This flag force using fake user media (synthetic video of multiple color)
        profile.setPreference("media.navigator.streams.fake", true);
        profile.setPreference("dom.file.createInChild", true);

        String eusApiURL = System.getenv("ET_EUS_API");


        options.setCapability("acceptInsecureCerts", true);


        if (eusApiURL == null) {

            options.setCapability("marionette", true);
            driver = new FirefoxDriver(options);

        } else {
            Map<String, Object> selenoidOptions = new HashMap<>();
            log.info("Using the remote WebDriver (Selenoid)");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");
            LocalDateTime now = LocalDateTime.now();
            String baseName = System.getProperty("tjob_name") + "-" + dtf.format(now) + "-" + testName + "-" + userIdentifier ;
            log.debug("The data of this test are stored into .mp4 and .log files named: {}", baseName);
            log.debug("Adding all the extra capabilities needed: {testName,enableVideo,enableVNC,name,enableLog,videoName,screenResolution}");
            //CAPABILITIES FOR SELENOID

            selenoidOptions.put("testName", testName + "-" + userIdentifier + "-" + dtf.format(now));
            selenoidOptions.put("enableVideo", true);
            selenoidOptions.put("enableVNC", true);
            selenoidOptions.put("name", testName + "-" + userIdentifier);
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
        }

        this.configureDriver();
    }


}