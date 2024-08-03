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

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class FirefoxUser extends BrowserUser {

    FirefoxOptions options = new FirefoxOptions();

    public FirefoxUser(String userIdentifier, int timeOfWaitInSeconds, String testName) throws URISyntaxException, MalformedURLException {
        super(userIdentifier, timeOfWaitInSeconds, testName);
        FirefoxProfile profile = new FirefoxProfile();
        // This flag avoids granting the access to the camera
        profile.setPreference("media.navigator.permission.disabled", true);
        // This flag force using fake user media (synthetic video of multiple color)
        profile.setPreference("media.navigator.streams.fake", true);
        profile.setPreference("dom.file.createInChild", true);
        options.setProfile(profile);
        options.setCapability("acceptInsecureCerts", true);

        if (System.getenv("SELENOID_PRESENT") == null) {
            log.info("SELENOID not present, using the local Firefox WebDriver(), setting additional capability (marionette)");
            options.setCapability("marionette", true);
            driver = new FirefoxDriver(options);
        } else {
            configureRemoteWebDriver(testName, options);
        }
        waitAndLastConfDriver();
    }

}