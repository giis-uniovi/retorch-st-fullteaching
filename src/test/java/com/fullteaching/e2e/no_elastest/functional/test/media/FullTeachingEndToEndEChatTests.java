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
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;

/**
 * E2E tests for FullTeaching chat in a video session.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */


@Tag("e2e")
@DisplayName("E2E tests for FullTeaching chat")
class FullTeachingEndToEndEChatTests extends BaseLoggedTest {

    private final static String STUDENT_BROWSER = "chrome";
    final String studentMail = "student1@gmail.com";
    final String studentPass = "pass";

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Configuration"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
    @DisplayName("oneToOneChatInSessionChrome")
    @ParameterizedTest
    @MethodSource("data")
    @Tag("Multiuser Test")
    void oneToOneChatInSessionChrome(String mail, String password, String role ) throws URISyntaxException, MalformedURLException { //197 Lines of code
        int numberpriormessages;

        // TEACHER
        this.slowLogin(user, mail, password);//24

        log.info("{} entering first course", user.getClientData());
        user.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
        user.getDriver().findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
                .click();

        log.info("{} navigating to 'Sessions' tab", user.getClientData());
        user.getWaiter().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
        user.getDriver().findElement(By.cssSelector("#md-tab-label-0-1")).click();

        log.info("{} getting into first session", user.getClientData());
        user.getDriver().findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();
        user.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fixed-icon")), "Element fixed-icon not clickable");
        // Check connected message
        user.getDriver().findElement(By.cssSelector("#fixed-icon")).click();

        checkSystemMessage("Connected", user, 100); // 6 lines
        // STUDENT
        student = setupBrowser(STUDENT_BROWSER, TJOB_NAME + "_" +"oneToOneChatInSessionChrome-STUDENT", studentMail,5);//27 lines

        this.slowLogin(student, studentMail, studentPass);

        student.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
        student.getDriver().findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
                .click();
        student.getWaiter().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
        student.getDriver().findElement(By.cssSelector("#md-tab-label-0-1")).click();

        student.getDriver().findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();
        student.getDriver().findElement(By.cssSelector("#fixed-icon")).click();

        checkSystemMessage("Connected", student, 0); //6 lines


        checkSystemMessage(STUDENT_NAME + " has connected", user, 100); //6 lines
        checkSystemMessage(TEACHER_NAME + " has connected", student, 100);//6lines
        // Test chat

        String teacherMessage = "TEACHER CHAT MESSAGE";
        String studentMessage = "STUDENT CHAT MESSAGE";
        numberpriormessages = getNumberMessages(user);
        WebElement chatInputTeacher = user.getDriver().findElement(By.id("message"));
        chatInputTeacher.sendKeys(teacherMessage);
        user.getWaiter().until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
        user.getDriver().findElement(By.id("send-btn")).click();

        checkOwnMessage(teacherMessage, TEACHER_NAME, user, numberpriormessages);//7 lines
        checkStrangerMessage(teacherMessage, TEACHER_NAME, student, numberpriormessages); //8lines
        numberpriormessages = getNumberMessages(student);
        WebElement chatInputStudent = student.getDriver().findElement(By.id("message"));
        chatInputStudent.sendKeys(studentMessage);
        student.getWaiter().until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
        student.getDriver().findElement(By.id("send-btn")).click();
        checkStrangerMessage(studentMessage, STUDENT_NAME, user, numberpriormessages); //8lines
        checkOwnMessage(studentMessage, STUDENT_NAME, student, numberpriormessages);//7lines


    }

    private void checkOwnMessage(String message, String sender, BrowserUser user, int numberpriormessages) { //7Lines
        log.info("Checking own message (\"{}\") for {}", message, user.getClientData());
        user.getWaiter().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), numberpriormessages));
        List<WebElement> messages = user.getDriver().findElements(By.tagName("app-chat-line"));
        WebElement lastMessage = messages.get(messages.size() - 1);
        WebElement msgUser = lastMessage.findElement(By.cssSelector(".own-msg .message-header .user-name"));
        WebElement msgContent = lastMessage.findElement(By.cssSelector(".own-msg .message-content .user-message"));
        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgContent, message));
    }

    private void checkStrangerMessage(String message, String sender, BrowserUser user, int numberpriormessages) {//8 Lines
        log.info("Checking another user's message (\"{}\") for {}", message, user.getClientData());
        user.getWaiter().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), numberpriormessages));
        List<WebElement> messages = user.getDriver().findElements(By.tagName("app-chat-line"));
        WebElement lastMessage = messages.get(messages.size() - 1);
        WebElement msgUser = lastMessage.findElement(By.cssSelector(".stranger-msg .message-header .user-name"));
        WebElement msgContent = lastMessage.findElement(By.cssSelector(".stranger-msg .message-content .user-message"));
        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgContent, message));
    }

    private void checkSystemMessage(String message, BrowserUser user, int messagenumber) { //6 lines
        log.info("Checking system message (\"{}\") for {}", message, user.getClientData());
        user.getWaiter().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));
        List<WebElement> messages = user.getDriver().findElements(By.tagName("app-chat-line"));
        WebElement lastMessage;
        if (messagenumber >= 0 && messagenumber < messages.size()) {
            lastMessage = messages.get(messagenumber);
        } else {
            lastMessage = messages.get(messages.size() - 1);
        }

        WebElement msgContent = lastMessage.findElement(By.cssSelector(".system-msg"));
        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgContent, message));
    }

    private int getNumberMessages(BrowserUser user) {
        return user.getDriver().findElements(By.tagName("app-chat-line")).size();
    }


}