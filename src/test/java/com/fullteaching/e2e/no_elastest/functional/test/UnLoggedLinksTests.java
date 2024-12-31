package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.SpiderNavigation;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import giis.retorch.annotations.AccessMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UnLoggedLinksTests extends BaseLoggedTest {

    public UnLoggedLinksTests() {
        super();
    }

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }

    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "openvidumock", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @AccessMode(resID = "course", concurrency = 15, sharing = true, accessMode = "READWRITE")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    @DisplayName("spiderUnloggedTest")
    void spiderUnloggedTest(String mail, String password, String role) { //125 lines + 28 set up +13 lines teardown = 166
        userMail = "nonloged@gmail.com";
        NavigationUtilities.getUrlAndWaitFooter(driver, HOST);
        List<WebElement> pageLinks = SpiderNavigation.getPageLinks(driver); //29 lines
        Map<String, String> explored = new HashMap<>();
        log.info("Navigate the links...");
        //Problem: once one is pressed the rest will be unusable as the page reloads...
        explored = SpiderNavigation.exploreLinks(driver, pageLinks, explored, DEPTH); //49 lines
        List<String> failed_links = new ArrayList<>();
        explored.forEach((link, result) -> {
            if (result.equals("KO")) failed_links.add(link);
        });

        String msg = "";
        for (String failed : failed_links) {
            msg = failed + "\n";
        }
        assertTrue(failed_links.isEmpty(), msg);
    }

}