package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import io.github.bonigarcia.wdm.managers.FirefoxDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class BaseLoggedTest {

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String EDGE = "edge";
    protected static final Class<? extends WebDriver> chrome = ChromeDriver.class;
    protected static final Class<? extends WebDriver> firefox = FirefoxDriver.class;
    public static String TEACHER_BROWSER;
    public static String STUDENT_BROWSER;
    public static String BROWSER_NAME;
    public static String course_title;
    protected static String HOST = LOCALHOST;
    protected static String userName;
    protected static String userMail;
    protected static String APP_URL;
    protected static Properties properties;
    protected static String TEST_NAME = "DEFAULT";
    protected static String TJOB_NAME = "TJobDef";
    protected final static int DEPTH = 3;
    public WebDriver driver;
    protected BrowserUser user;

    public static final Logger log = LoggerFactory.getLogger(BaseLoggedTest.class);

    public BaseLoggedTest() {
    }

    @BeforeAll
    static void setupAll() { // 28 lines
        // Initialize properties
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/test/resources/inputs/test.properties"));
        } catch (IOException ex) {
            log.error("The properties file cannot be loaded, cause {}",ex.getStackTrace().toString());
        }
        // The existence of the ET_EUS_API indicates that its being executed in Jenkins, so set-up the chrom and firefox instances
        if (System.getenv("ET_EUS_API") == null) {
            // Setup drivers for Chrome and Firefox
            ChromeDriverManager.getInstance(chrome).setup();
            FirefoxDriverManager.getInstance(firefox).setup();
        }

        String envUrl = System.getProperty("SUT_URL") != null ? System.getProperty("SUT_URL") : System.getenv("SUT_URL");
        String envPort = System.getProperty("SUT_PORT") != null ? System.getProperty("SUT_PORT") : System.getenv("SUT_PORT");
        String envTJobName = System.getProperty("tjob_name") != null ? System.getProperty("tjob_name") : System.getenv("tjob_name");
        log.info("Using URL {} PORT: {} TJOB: {}", envUrl, envPort, envTJobName);
        // Check if SUT_URL is defined in the environment variables
        if ((envUrl != null) & (envPort != null)) {
            TJOB_NAME = envTJobName;
            PORT = envPort;
            APP_URL = envUrl + TJOB_NAME + ":" + PORT + "/";
            log.debug("The URL is {}" , APP_URL);
            HOST = APP_URL;
        } else {
            // Check if app.url system property is defined
            APP_URL = System.getProperty("app.url", LOCALHOST);
            // Set HOST if APP_URL is not null
            if (APP_URL != null) {
                HOST = APP_URL;
            }
        }
        // Set browser types for teacher and student
        TEACHER_BROWSER = System.getenv("TEACHER_BROWSER");
        STUDENT_BROWSER = System.getenv("STUDENT_BROWSER");
        // Default to Chrome if browser types are not defined or are not Firefox
        TEACHER_BROWSER = (TEACHER_BROWSER == null || !TEACHER_BROWSER.equals(FIREFOX)) ? CHROME : TEACHER_BROWSER;
        STUDENT_BROWSER = (STUDENT_BROWSER == null || !STUDENT_BROWSER.equals(FIREFOX)) ? CHROME : STUDENT_BROWSER;
        log.info("Using URL {} to connect to OpenVidu-app", APP_URL);
    }

    @BeforeEach
    void setup(TestInfo info) { //65 lines
        if (info.getTestMethod().isPresent()) {
            TEST_NAME = info.getTestMethod().get().getName();
        }
        log.info("##### Start test: {}", TEST_NAME);
        TJOB_NAME = System.getProperty("dirtarget");
        user = setupBrowser("chrome", TEST_NAME, userMail, WAIT_SECONDS);
        driver = user.getDriver();
    }

    protected BrowserUser setupBrowser(String browser, String testName,
                                       String userIdentifier, int secondsOfWait) {
        BrowserUser u;
        log.info("Starting browser ({})", browser);

        switch (browser) {
            case FIREFOX:
                BROWSER_NAME = FIREFOX;
                u = new FirefoxUser(secondsOfWait, testName,
                        userIdentifier);
                break;
            case EDGE:
                BROWSER_NAME = EDGE;
                u = new EdgeUser(secondsOfWait, testName,
                        userIdentifier);
                break;
            default:
                BROWSER_NAME = CHROME;
                u = new ChromeUser(secondsOfWait, testName,
                        userIdentifier);
        }
        log.info("Navigating to {}", APP_URL);

        u.getDriver().get(APP_URL);
        final String GLOBAL_JS_FUNCTION = "var s = window.document.createElement('script');"
                + "s.innerText = 'window.MY_FUNC = function(containerQuerySelector) {"
                + "var elem = document.createElement(\"div\");"
                + "elem.id = \"video-playing-div\";"
                + "elem.innerText = \"VIDEO PLAYING\";"
                + "document.body.appendChild(elem);"
                + "console.log(\"Video check function successfully added to DOM by Selenium\")}';"
                + "window.document.head.appendChild(s);";
        u.runJavascript(GLOBAL_JS_FUNCTION);

        return u;
    }

    @AfterEach
    void tearDown(TestInfo info) { //13 lines

        if (user != null) {
            log.info("##### Finish test: {} - Driver {}", info.getTestMethod().get().getName(), this.user.getDriver());
            log.info("Browser console at the end of the test");
            LogEntries logEntries = user.getDriver().manage().logs().get(BROWSER);
            logEntries.forEach(entry -> log.info("[{}] {} {}",
                    new Date(entry.getTimestamp()), entry.getLevel(),
                    entry.getMessage()));
            if (user.isOnSession()) {
                this.logout(user);
            }
            user.dispose();
        }
    }

    protected void slowLogin(BrowserUser user, String userEmail,
                             String userPass) {//24 lines
        log.info("Slow login");
        this.login(user, userEmail, userPass, true);
    }

    protected void quickLogin(BrowserUser user, String userEmail,
                              String userPass) { //24 lines
        log.info("Quick login");
        this.login(user, userEmail, userPass, false);
    }

    private void login(BrowserUser user, String userEmail, String userPass,
                       boolean slow) { //24 lines
        user.setOnSession(true);
        if(user.getClientData()==null){user.setClientData(userEmail);}
        log.info("Logging in user {} with mail '{}'", user.getClientData(), userEmail);
        Wait.waitForPageLoaded(user.getDriver());
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("#download-button")), "The button searched by CSS #download-button is not clickable");
        openDialog("#download-button", user);
        Wait.waitForPageLoaded(user.getDriver());
        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("email")), "The email field is not present");
        // Find form elements (login modal is already opened)
        WebElement userNameField = user.getDriver().findElement(By.id("email"));
        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("password")), "The password field is not present");
        WebElement userPassField = user.getDriver().findElement(By.id("password"));
        // Fill input fields
        userNameField.sendKeys(userEmail);
        if (slow)
            waitSeconds(3);
        userPassField.sendKeys(userPass);
        if (slow)
            waitSeconds(3);
        // Ensure fields contain what has been entered
        Assertions.assertEquals(userNameField.getAttribute("value"), userEmail);
        Assertions.assertEquals(userPassField.getAttribute("value"), userPass);
        user.getDriver().findElement(By.id("log-in-btn")).click();

        user.waitUntil(ExpectedConditions.presenceOfElementLocated(COURSE_LIST), "The Course list is not present");
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("course-list"))), "Course list is not clickable");
        try {
            userName = getUserName(user, true, APP_URL);
        } catch (NotLoggedException | ElementNotFoundException e) {
            if (e instanceof NotLoggedException) {
                log.error("The user {} is not logged in", user.getClientData());}
            else {
                log.error("The web element with the user data was not found");
            }
        }
        log.info("Logging in successful for user {}", user.getClientData());
    }

    protected void logout(BrowserUser user) { //43 lines
        log.info("Logging out {}", user.getClientData());

        if (!user.getDriver().findElements(By.cssSelector("#fixed-icon")).isEmpty()) {
            // Get out of video session page
            if (!isClickable("#exit-icon", user)) { // Side menu not opened
                user.getDriver().findElement(By.cssSelector("#fixed-icon"))
                        .click();
            }
            user.getWaiter().until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector("#exit-icon")));
            user.getDriver().findElement(By.cssSelector("#exit-icon")).click();
        }
        try {
            // Up bar menu
            user.getWaiter()
                    .until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("#arrow-drop-down")));
            user.getDriver().findElement(By.cssSelector("#arrow-drop-down"))
                    .click();

            user.getWaiter().until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector("#logout-button")));
            user.getDriver().findElement(By.cssSelector("#logout-button"))
                    .click();
        } catch (TimeoutException e) {
            // Shrunk menu
            user.getWaiter()
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("a.button-collapse")));
            user.getDriver().findElement(By.cssSelector("a.button-collapse"))
                    .click();

            user.getWaiter().until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='nav-mobile']//a[text() = 'Logout']")));
            user.getDriver()
                    .findElement(By.xpath(
                            "//ul[@id='nav-mobile']//a[text() = 'Logout']"))
                    .click();
        }
        user.setOnSession(false);
        log.info("Logging out successful for {}", user.getClientData());

    }

    private boolean isClickable(String selector, BrowserUser user) {
        try {
            user.getWaiter().until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector(selector)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void openDialog(String cssSelector, BrowserUser user) {
        Wait.waitForPageLoaded(user.getDriver());

        log.info("User {} opening dialog by clicking CSS '{}'",
                user.getClientData(), cssSelector);

        Wait.waitForPageLoaded(user.getDriver());
        user.waitUntil(
                ExpectedConditions
                        .elementToBeClickable(By.cssSelector(cssSelector)),
                "Button for opening the dialog not clickable");

        user.getDriver().findElement(By.cssSelector(cssSelector)).click();
        Wait.waitForPageLoaded(user.getDriver());
        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")),
                "Dialog not opened");

        log.info("Dialog opened by clicking CSS  for user {}", user.getClientData());
    }

    protected void openDialog(WebElement el, BrowserUser user) {//8lines
        log.info("User {} opening dialog by web element '{}'",
                user.getClientData(), el);
        user.waitUntil(ExpectedConditions.elementToBeClickable(el),
                "Button for opening the dialog not clickable");
        el.click();
        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")),
                "Dialog not opened");
        log.info("Dialog opened by web element for user {}", user.getClientData());
    }

    protected void waitForDialogClosed(String dialogId, String errorMessage,
                                       BrowserUser user) {//14 lines
        log.info("User {} waiting for dialog with id '{}' to be closed",
                user.getClientData(), dialogId);
        user.waitUntil(ExpectedConditions
                        .presenceOfElementLocated(By.xpath("//div[@id='" + dialogId
                                + "' and contains(@class, 'my-modal-class') and contains(@style, 'opacity: 0') and contains(@style, 'display: none')]")),
                "Dialog not closed. Reason: " + errorMessage);
        user.waitUntil(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".modal.my-modal-class.open")),
                "Dialog not closed. Reason: " + errorMessage);
        user.waitUntil(
                ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector(".modal-overlay"), 0),
                "Dialog not closed. Reason: " + errorMessage);
        log.info("Dialog closed for user {}", user.getClientData());
    }

    protected void waitSeconds(int seconds) {
        try {
            Thread.sleep(1000L * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getUserName(BrowserUser user, boolean goBack, String host) throws NotLoggedException, ElementNotFoundException {
        log.info("[INI]getUserName");
        //Wait to settings button to be present
        try {
            Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_BUTTON));
            WebElement settings_button = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.elementToBeClickable(SETTINGS_BUTTON));

            if (NavigationUtilities.amINotHere(user.getDriver(), host + "/settings")) {
                Click.element(user.getDriver(), settings_button);
            } else {
                goBack = false;
            }

        } catch (TimeoutException toe) {
            throw new NotLoggedException(toe.getMessage());
        }

        WebElement name_placeholder = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(USERNAME_XPATH)));

        String userUIName = name_placeholder.getText().trim();

        if (goBack) {
            user.getDriver().navigate().back();
        }
        log.info("[END] getUserName");

        return userUIName;
    }
}