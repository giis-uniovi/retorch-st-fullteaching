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

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class EdgeUser extends BrowserUser {

    EdgeOptions options = new EdgeOptions();

    public EdgeUser(String userIdentifier, int timeOfWaitInSeconds, String testName) throws URISyntaxException, MalformedURLException {
        super(userIdentifier, timeOfWaitInSeconds, testName);
        log.info("Starting the configuration of Edge Web Browser, setting the capabilities");
        //This capability is to store the logs of the test case
        //Some tests report an error due to a non-trust server, this capability avoid it
        options.setAcceptInsecureCerts(true);

        if (System.getenv("SELENOID_PRESENT") == null) {
            log.info("SELENOID not present, using the local EdgeDriver") ;
            this.driver = new EdgeDriver(options);
        } else {
            configureRemoteWebDriver(testName, options);
        }
        waitAndLastConfDriver();
    }

}