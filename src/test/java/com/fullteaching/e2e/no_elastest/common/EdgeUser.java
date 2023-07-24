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

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EdgeUser extends BrowserUser {
    EdgeOptions options = new EdgeOptions();

    public EdgeUser(String userName, int timeOfWaitInSeconds, String testName, String userIdentifier) {
        super(userName, timeOfWaitInSeconds);
        log.info(String.format("The Test names are: %s", testName));


        String eusApiURL = System.getenv("ET_EUS_API");
        //This capability is to store the logs of the test case

        //Some tests report an error due to a non-trust server, this capability avoid it
        options.setAcceptInsecureCerts(true);


        if (eusApiURL == null) {
            this.driver = new EdgeDriver(options);
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
                selenoidOptions.put("name", testName + "_" + userIdentifier);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");
                LocalDateTime now = LocalDateTime.now();
                String logName = dtf.format(now) + "-" + testName + "-" + userIdentifier + ".log";
                String videoName = dtf.format(now) + "_" + testName + "_" + userIdentifier + ".mp4";
                log.debug("The data of this test would be stored into: video name " + videoName + " and the log is " + logName);

                selenoidOptions.put("enableLog", true);
                selenoidOptions.put("logName ", logName);
                selenoidOptions.put("videoName", videoName);

                selenoidOptions.put("screenResolution", "1920x1080x24");

                options.setCapability("selenoid:options", selenoidOptions);

                //END CAPABILITIES FOR SELENOID RETORCH

                RemoteWebDriver remote = new RemoteWebDriver(new URL(eusApiURL), options);
                remote.setFileDetector(new LocalFileDetector());


                this.driver = remote;
            } catch (MalformedURLException e) {
                throw new RuntimeException("Exception creating eusApiURL", e);
            }
        }

        new WebDriverWait(driver, Duration.ofSeconds(this.timeOfWaitInSeconds));

        this.configureDriver();
    }

}