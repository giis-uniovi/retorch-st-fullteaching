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

package com.fullteaching.e2e.no_elastest.functional.test.media;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.BrowserUser;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.stream.Stream;


/*This test case were disabled due to problems with the OpenVidu Server. The video input doesn't work , so it's not
 * feasible check that in the other side of the connection its playing the stream*/
@Disabled
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching video session")
class FullTeachingTestEndToEndVideoSessionTests extends BaseLoggedTest {

    final String studentMail = "student1@gmail.com";
    final String studentPass = "pass";

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }

    public FullTeachingTestEndToEndVideoSessionTests() {
        super();
    }


    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @DisplayName("sessionTest")
    @ParameterizedTest
    @MethodSource("data")
    void oneToOneVideoAudioSessionChrome(String mail, String password, String role) throws URISyntaxException, MalformedURLException, NotLoggedException, ElementNotFoundException, InterruptedException { //124+ 232+ 20 set up +8 lines teardown = 564

        // TEACHER
        this.slowLogin(user, mail, password);//24

        log.info("{} entering first course", user.getClientData());
        user.getWaiter().until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(
                        ("ul.collection li.collection-item:first-child div.course-title"))));
        user.getDriver().findElement(By.cssSelector(
                        "ul.collection li.collection-item:first-child div.course-title"))
                .click();

        log.info("{} navigating to 'Sessions' tab", user.getClientData());
        user.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("#md-tab-label-0-1"))));
        user.getDriver().findElement(By.cssSelector("#md-tab-label-0-1"))
                .click();

        log.info("{} getting into first session", user.getClientData());
        user.getDriver()
                .findElement(By.cssSelector(
                        "ul div:first-child li.session-data div.session-ready"))
                .click();

        user.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("div.participant video"))));

        checkVideoPlaying(user,
                user.getDriver()
                        .findElement(By.cssSelector(("div.participant video"))),
                "div.participant"); //30 lines
        // STUDENT
        student = setupBrowser(STUDENT_BROWSER, TJOB_NAME + "-" +TEST_NAME, "STUDENT",5);//27 lines

        slowLogin(student, studentMail, studentPass);

        log.info("{} entering first course", student.getClientData());
        student.getWaiter().until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(
                        ("ul.collection li.collection-item:first-child div.course-title"))));
        student.getDriver().findElement(By.cssSelector(
                        "ul.collection li.collection-item:first-child div.course-title"))
                .click();

        log.info("{} navigating to 'Courses' tab", student.getClientData());
        student.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("#md-tab-label-0-1"))));
        student.getDriver().findElement(By.cssSelector("#md-tab-label-0-1"))
                .click();

        log.info("{} getting into first session", student.getClientData());
        student.getDriver().findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();

        student.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("div.participant video"))));

        checkVideoPlaying(student,
                student.getDriver()
                        .findElement(By.cssSelector(("div.participant video"))),
                "div.participant");//30 lines
        // Student asks for intervention
        student.getWaiter().until(ExpectedConditions.elementToBeClickable(By
                .xpath("//div[@id='div-header-buttons']//i[text() = 'record_voice_over']")));
        log.info("{} asking for intervention", student.getClientData());
        student.getDriver().findElement(By.xpath(
                        "//div[@id='div-header-buttons']//i[text() = 'record_voice_over']"))
                .click();

        // Teacher accepts intervention
        user.getWaiter().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@class, 'usr-btn')]")));
        log.info("{} accepts student intervention", user.getClientData());
        user.getDriver()
                .findElement(By.xpath("//a[contains(@class, 'usr-btn')]"))
                .click();
        // Check both videos for both users
        student.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("div.participant-small video"))));
        // Small video of student
        checkVideoPlaying(student,
                student.getDriver().findElement(
                        By.cssSelector(("div.participant-small video"))),
                "div.participant-small");//30 lines
        // Main video of student
        checkVideoPlaying(student,
                student.getDriver()
                        .findElement(By.cssSelector(("div.participant video"))),
                "div.participant");//30 lines
        user.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("div.participant-small video"))));
        // Small video of teacher
        checkVideoPlaying(user,
                user.getDriver().findElement(
                        By.cssSelector(("div.participant-small video"))),
                "div.participant-small");//30 lines
        // Main video of teacher
        checkVideoPlaying(user,
                user.getDriver()
                        .findElement(By.cssSelector(("div.participant video"))),
                "div.participant");
        // Teacher stops student intervention
        user.getWaiter().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@class, 'usr-btn')]")));
        log.info("{} canceling student intervention", user.getClientData());
        user.getDriver()
                .findElement(By.xpath("//a[contains(@class, 'usr-btn')]"))
                .click();
        // Wait until only one video
        user.getWaiter().until(ExpectedConditions
                .not(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(("div.participant-small video")))));
        student.getWaiter().until(ExpectedConditions
                .not(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(("div.participant-small video")))));


    }



    /*
     * @Test
     *
     * @DisplayName("Cross-Browser test") void crossBrowserTest() throws
     * Exception {
     *
     * setupBrowser("chrome");
     *
     * log.info("Cross-Browser test");
     *
     * Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler()
     * { public void uncaughtException(Thread th, Throwable ex) {
     * System.out.println("Uncaught exception: " + ex); synchronized (lock) {
     * OpenViduTestAppE2eTest.ex = new Exception(ex); } } };
     *
     * Thread t = new Thread(() -> { BrowserUser user2 = new
     * FirefoxUser("TestUser", 30); user2.getDriver().get(APP_URL); WebElement
     * urlInput = user2.getDriver().findElement(By.id("openvidu-url"));
     * urlInput.clear(); urlInput.sendKeys(OPENVIDU_URL); WebElement secretInput
     * = user2.getDriver().findElement(By.id("openvidu-secret"));
     * secretInput.clear(); secretInput.sendKeys(OPENVIDU_SECRET);
     *
     * user2.getEventManager().startPolling();
     *
     * user2.getDriver().findElement(By.id("add-user-btn")).click();
     * user2.getDriver().findElement(By.className("join-btn")).click(); try {
     * user2.getEventManager().waitUntilNumberOfEvent("videoPlaying", 2);
     * Assert.assertTrue(user2.getEventManager()
     * .assertMediaTracks(user2.getDriver().findElements(By.tagName("video")),
     * true, true));
     * user2.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 1);
     * user2.getDriver().findElement(By.id("remove-user-btn")).click();
     * user2.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
     * } catch (Exception e) { e.printStackTrace();
     * Thread.currentThread().interrupt(); } user2.dispose(); });
     * t.setUncaughtExceptionHandler(h); t.start();
     *
     * user.getDriver().findElement(By.id("add-user-btn")).click();
     * user.getDriver().findElement(By.className("join-btn")).click();
     *
     * user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 2);
     *
     * try { System.out.println(getBase64Screenshot(user)); } catch (Exception
     * e) { e.printStackTrace(); }
     *
     * Assert.assertTrue(user.getEventManager().assertMediaTracks(user.getDriver
     * (). findElements(By.tagName("video")), true, true));
     *
     * user.getDriver().findElement(By.id("remove-user-btn")).click();
     *
     * user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
     *
     * t.join();
     *
     * synchronized (lock) { if (OpenViduTestAppE2eTest.ex != null) { throw
     * OpenViduTestAppE2eTest.ex; } } }
     */

    private void checkVideoPlaying(BrowserUser user, WebElement videoElement,
                                   String containerQuerySelector) { //30 lines
        log.info("{} waiting for video in container '{}' to be playing",
                user.getClientData(), containerQuerySelector);
        // Video element should be in 'readyState'='HAVE_ENOUGH_DATA'
        user.getWaiter().until(ExpectedConditions.attributeToBe(videoElement,
                "readyState", "4"));
        // Video should have a srcObject (type MediaStream) with the attribute
        // 'active'
        // to true
        Assertions.assertTrue((boolean) user.runJavascript(
                "return document.querySelector('" + containerQuerySelector
                        + "').getElementsByTagName('video')[0].srcObject.active"));
        // Video should trigger 'playing' event
        user.runJavascript("document.querySelector('" + containerQuerySelector
                + "').getElementsByTagName('video')[0].addEventListener('playing', window.MY_FUNC('"
                + containerQuerySelector + "'));");
        user.getWaiter().until(ExpectedConditions.attributeContains(
                By.id("video-playing-div"), "innerHTML", "VIDEO PLAYING"));
        user.runJavascript(
                "document.body.removeChild(document.getElementById('video-playing-div'))");
    }

}
