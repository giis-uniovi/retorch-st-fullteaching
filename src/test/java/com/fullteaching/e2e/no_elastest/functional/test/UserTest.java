package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import giis.retorch.annotations.AccessMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest extends BaseLoggedTest {

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }

    /**
     * This test is a simple logging acknowledgement, that checks if the current logged user
     * was logged correctly
     */

    @AccessMode(resID = "loginservice", concurrency = 10, sharing = true, accessMode = "READONLY")
    @AccessMode(resID = "openvidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @AccessMode(resID = "executor", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webbrowser", concurrency = 1, accessMode = "READWRITE")
    @AccessMode(resID = "webserver", concurrency = 1, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    @DisplayName("loginTest")
    void loginTest(String mail, String password, String role) throws NotLoggedException, ElementNotFoundException, BadUserException, InterruptedException { //22  +85 +28 set up +13 lines teardown  =148
            this.slowLogin(user, mail, password); //24 lines
            UserUtilities.checkLogin(driver, mail); //12 lines
            assertTrue(true, "not logged");

            this.logout(user); //14 lines
            UserUtilities.checkLogOut(driver); //8lines
            assertTrue(true);
    }
}