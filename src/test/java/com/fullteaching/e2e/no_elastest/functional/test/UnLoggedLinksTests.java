package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.SpiderNavigation;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

class UnLoggedLinksTests extends BaseLoggedTest {

    protected final static int DEPTH = 3;

    final static Logger log = getLogger(lookup().lookupClass());


    public UnLoggedLinksTests() {
        super();
    }

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }


    @ParameterizedTest
    @MethodSource("data")
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "READWRITE")
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
