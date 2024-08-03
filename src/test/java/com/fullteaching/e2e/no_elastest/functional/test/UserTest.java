package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import giis.retorch.annotations.AccessMode;
import giis.retorch.annotations.Resource;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SeleniumJupiter.class)
class UserTest extends BaseLoggedTest {

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }

    /**
     * This test is a simple logging acknowledgement, that checks if the current logged user
     * was logged correctly
     */

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
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