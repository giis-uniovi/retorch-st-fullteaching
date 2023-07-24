package com.fullteaching.e2e.no_elastest.common;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public final class Constants {
    public static final int WAIT_SECONDS = 150;
    public static final String LOCALHOST = "https://localhost:5000";
    public static final String PORT = "11811";
    public static final String STUDENT_NAME = "Student Imprudent";
    public static final String TEACHER_NAME = "Teacher Cheater";

    public static final String COURSES_URL = "__HOST__/courses";

    //Xpath and ids
    public static final String FOOTER_CLASS = "page-footer";
    public static final String MAIN_MENU_ARROW_ID = "arrow-drop-down";
    public static final String LOGOUT_BUTTON_ID = "logout-button";

    public static final String LOGIN_MODAL_ID = "login-modal";
    public static final String LOGIN_MODAL_USER_FIELD_ID = "email";
    public static final String LOGIN_MODAL_PASSWORD_FIELD_ID = "password";
    public static final String LOGIN_MODAL_LOGIN_BUTTON_ID = "log-in-btn";


    public static final String COURSES_DASHBOARD_TITLE_CLASS = "dashboard-title";


    public static final String FIRST_COURSE_XPATH = "/html/body/app/div/main/app-dashboard/div/div[3]/div/div[1]/ul/li[1]";
    public static final String GO_TO_COURSE_XPATH = "/div[2]"; /*use with XCOURSE_XPATH+GOTOCOURSE_XPATH*/
    public static final String COURSE_LIST_COURSE_TITLE_CLASS = "course-title";
    public static final String COURSE_LIST_CLASS = "dashboard-col";

    public static final String TABS_DIV_ID = "tabs-course-details";

    public static final String NEW_COURSE_BUTTON_ID = "add-new-course-btn";
    public static final String NEW_COURSE_MODAL_ID = "course-modal";
    public static final String NEW_COURSE_MODAL_NAME_FIELD_ID = "input-post-course-name";
    public static final String NEW_COURSE_MODAL_SAVE_ID = "submit-post-course-btn";

    public static final String EDIT_COURSE_BUTTON_CLASS = "course-put-icon";
    public static final String EDIT_DELETE_MODAL_ID = "put-delete-course-modal";
    public static final String EDIT_COURSE_MODAL_NAME_FIELD_ID = "input-put-course-name";
    public static final String EDIT_COURSE_MODAL_SAVE_ID = "submit-put-course-btn";
    public static final String EDIT_COURSE_DELETE_CHECK_ID = "label-delete-checkbox";
    public static final String EDIT_COURSE_DELETE_BUTTON_ID = "delete-course-btn";

    public static final String COURSE_BACK_TO_DASHBOARD_CLASS = "btn-floating";
    public static final String COURSE_TABS_TAG = "tabs-course-details";


    //It's the original, changeD because don't work
    //public static final String COURSE_TABS_TAG = "md-tab-group";


    /*FROM mat-tab-group*/
    public static final String FORUM_TAB_XPATH = "./div[1]/div[3]";
    /*FROM mat-tab-group*/
    public static final String HOME_ICON_ID = "info-tab-icon";
    public static final String SESSION_ICON_ID = "sessions-tab-icon";
    public static final String FORUM_ICON_ID = "forum-tab-icon";
    public static final String FILES_ICON_ID = "files-tab-icon";
    public static final String ATTENDERS_ICON_ID = "attenders-tab-icon";

    public static final String SESSION_LIST_NEW_SESSION_ICON_CLASS = "add-element-icon";

    public static final String EDIT_DESCRIPTION_BUTTON_ID = "edit-course-info";
    public static final String EDIT_DESCRIPTION_CONTENT_BOX_CLASS = "ui-editor-content";

    public static final String EDIT_DESCRIPTION_SAVE_BUTTON_ID = "send-info-btn";

    public static final String USERNAME_XPATH = "/html/body/app/div/main/app-settings/div/div[3]/div[2]/ul/li[2]/div[2]";
    public static final String LOGIN_MENU_XPATH = "/html/body/app/div/header/navbar/div/nav/div/ul/li[2]/a";

    public static final String ENABLE_FORUM_BUTTON_ID = "label-forum-checkbox";
    public static final String ENABLE_FORUM_MODAL_SAVE_BUTTON_ID = "put-modal-btn";
    public static final String ENABLE_FORUM_MODAL_ID = "put-delete-modal";

    public static final String ENABLE_COURSE_DELETION_BUTTON_XPATH = "/html/body/app/div/com.fullteaching.e2e.no_elastest.main/app-dashboard/div/div[2]/div/div/form/div[2]/div/div/label";
    public static final String DELETE_COURSE_BUTTON_XPATH = "/html/body/app/div/com.fullteaching.e2e.no_elastest.main/app-dashboard/div/div[2]/div/div/form/div[2]/div/a";

    public static final String COURSES_BUTTON_ID = "courses-button";
    public static final String SETTINGS_BUTTON_ID = "settings-button";

    public static final String FORUM_NEW_ENTRY_ICON_ID = "add-entry-icon";
    public static final String FORUM_EDIT_ENTRY_ICON_ID = "edit-forum-icon";
    public static final String FORUM_ENTRY_LIST_ENTRY_TITLE_CLASS = "forum-entry-title"; //from each li in the list of entries
    public static final String FORUM_ENTRY_LIST_ENTRIES_UL_CLASS = "entries-side-view";
    public static final String FORUM_ENTRY_LIST_ENTRY_USER_CLASS = "user-name";
    public static final String FORUM_COMMENT_LIST_ENTRY_TITLE_CLASS = "comment-section-title";
    public static final String FORUM_COMMENT_LIST_ENTRY_USER_CLASS = "user-name";
    public static final String FORUM_COMMENT_LIST_ID = "row-of-comments";
    public static final String FORUM_COMMENT_LIST_COMMENT_CLASS = "comment-block";
    public static final String FORUM_COMMENT_LIST_COMMENT_USER_CLASS = "user-name";
    public static final String FORUM_COMMENT_LIST_COMMENT_CONTENT_CLASS = "message-itself";
    public static final String FORUM_COMMENT_LIST_BACK_TO_ENTRIES_LIST_ICON_ID = "entries-sml-btn";
    public static final String FORUM_NEW_ENTRY_MODAL_ID = "course-details-modal";
    public static final String FORUM_NEW_ENTRY_MODAL_TITLE_ID = "input-post-title";
    public static final String FORUM_NEW_ENTRY_MODAL_CONTENT_ID = "input-post-comment";
    public static final String FORUM_NEW_ENTRY_MODAL_POST_BUTTON_ID = "post-modal-btn";
    public static final String FORUM_COMMENT_LIST_NEW_COMMENT_ICON_CLASS = "forum-icon";
    public static final String FORUM_NEW_COMMENT_MODAL_ID = "course-details-modal";
    public static final String FORUM_NEW_COMMENT_MODAL_TEXT_FIELD_ID = "input-post-comment";
    public static final String FORUM_NEW_COMMENT_MODAL_POST_BUTTON_ID = "post-modal-btn";
    public static final String FORUM_COMMENT_LIST_COMMENT_REPLY_ICON_CLASS = "replay-icon";
    public static final String FORUM_COMMENT_LIST_MODAL_NEW_REPLY_ID = "course-details-modal";
    public static final String FORUM_COMMENT_LIST_MODAL_NEW_REPLY_TEXT_FIELD_ID = "input-post-comment";
    public static final String FORUM_COMMENT_LIST_COMMENT_DIV_CLASS = "comment-div";

    public static final String SESSION_LIST_NEW_SESSION_MODAL_ID = "course-details-modal";
    public static final String SESSION_LIST_NEW_SESSION_MODAL_TITLE_ID = "input-post-title";
    public static final String SESSION_LIST_NEW_SESSION_MODAL_CONTENT_ID = "input-post-comment";
    public static final String SESSION_LIST_NEW_SESSION_MODAL_DATE_ID = "input-post-date";
    public static final String SESSION_LIST_NEW_SESSION_MODAL_TIME_ID = "input-post-time";
    public static final String SESSION_LIST_NEW_SESSION_MODAL_POST_BUTTON_ID = "post-modal-btn";
    public static final String SESSION_LIST_SESSION_ROW_CLASS = "session-data";
    public static final String SESSION_LIST_SESSION_NAME_CLASS = "session-title";
    public static final String SESSION_LIST_SESSION_ACCESS_CLASS = "session-ready";
    public static final String SESSION_LIST_SESSION_EDIT_ICON_CLASS = "forum-icon";
    public static final String SESSION_LIST_EDIT_MODAL_ID = "put-delete-modal";
    public static final String SESSION_LIST_EDIT_MODAL_DELETE_DIV_CLASS = "delete-div";

    public static final String SESSION_LEFT_MENU_BUTTON_ID = "side-menu-button";
    public static final String SESSION_EXIT_ICON_ID = "exit-icon";

    public static final String ATTENDERS_LIST_ROWS_CLASS = "attender-row-div";
    public static final String ATTENDERS_LIST_HIGHLIGHTED_ROW_CLASS = "attender-name-p";

    public static final String SETTINGS_USER_EMAIL_ID = "stng-user-mail";

    //BUTTONS
    public static final By NEW_COURSE_BUTTON = By.id(NEW_COURSE_BUTTON_ID);
    public static final By SETTINGS_BUTTON = By.id(SETTINGS_BUTTON_ID);
    public static final By COURSES_BUTTON = By.id(COURSES_BUTTON_ID);
    public static final By LOGOUT_BUTTON = By.id(LOGOUT_BUTTON_ID);

    public static final By LOGIN_BUTTON = By.id(LOGIN_MODAL_LOGIN_BUTTON_ID);

    public static final By EDIT_COURSE_BUTTON = By.className(EDIT_COURSE_BUTTON_CLASS);
    public static final By EDIT_COURSE_MODAL_SAVE = By.id(EDIT_COURSE_MODAL_SAVE_ID);

    public static final By NEW_COURSE_MODAL_SAVE = By.id(NEW_COURSE_MODAL_SAVE_ID);
    public static final By EDIT_DESCRIPTION_BUTTON = By.id(EDIT_DESCRIPTION_BUTTON_ID);
    public static final By EDIT_DESCRIPTION_SAVE_BUTTON = By.id(EDIT_DESCRIPTION_SAVE_BUTTON_ID);

    public static final By DISABLE_FORUM_BUTTON = By.id(ENABLE_FORUM_BUTTON_ID);
    public static final By ENABLE_FORUM_BUTTON = By.id(ENABLE_FORUM_BUTTON_ID);
    public static final By ENABLE_FORUM_MODAL = By.id(ENABLE_FORUM_MODAL_ID);


    public static final By BACK_TO_DASHBOARD = By.className(COURSE_BACK_TO_DASHBOARD_CLASS);

    public static final By FORUM_NEW_ENTRY_MODAL_POST_BUTTON = By.id(FORUM_NEW_ENTRY_MODAL_POST_BUTTON_ID);
    public static final By ENABLE_FORUM_MODAL_SAVE_BUTTON = By.id(ENABLE_FORUM_MODAL_SAVE_BUTTON_ID);

    public static final By SESSION_LIST_NEW_SESSION_MODAL_POST_BUTTON = By.id(SESSION_LIST_NEW_SESSION_MODAL_POST_BUTTON_ID);

    public static final By SESSION_LEFT_MENU_BUTTON = By.id(SESSION_LEFT_MENU_BUTTON_ID);

    //TABS

    //MODALS
    public static final By NEW_COURSE_MODAL = By.id(NEW_COURSE_MODAL_ID);
    public static final By EDIT_DELETE_MODAL = By.id(EDIT_DELETE_MODAL_ID);
    public static final By LOGIN_MODAL = By.id(LOGIN_MODAL_ID);
    public static final By FORUM_NEW_ENTRY_MODAL = By.id(FORUM_NEW_ENTRY_MODAL_ID);
    public static final By FORUM_NEW_COMMENT_MODAL = By.id(FORUM_NEW_COMMENT_MODAL_ID);
    public static final By SESSION_LIST_NEW_SESSION_MODAL = By.id(SESSION_LIST_NEW_SESSION_MODAL_ID);
    public static final By SESSION_LIST_EDIT_MODAL = By.id(SESSION_LIST_EDIT_MODAL_ID);

    //OTHER ELEMENTS
    public static final By FOOTER = By.className(FOOTER_CLASS);
    public static final By MAIN_MENU_ARROW = By.id(MAIN_MENU_ARROW_ID);

    public static final By LOGIN_USER_FIELD = By.id(LOGIN_MODAL_USER_FIELD_ID);
    public static final By LOGIN_PASSWORD_FIELD = By.id(LOGIN_MODAL_PASSWORD_FIELD_ID);

    public static final By SETTINGS_USER_EMAIL = By.id(SETTINGS_USER_EMAIL_ID);

    public static final By COURSES_DASHBOARD_TITLE = By.className(COURSES_DASHBOARD_TITLE_CLASS);
    public static final By COURSE_LIST = By.className(COURSE_LIST_CLASS);


    public static final By COURSE_TABS = By.id(COURSE_TABS_TAG);
    public static final By EDIT_COURSE_DELETE_CHECK = By.id(EDIT_COURSE_DELETE_CHECK_ID);
    public static final By EDIT_COURSE_DELETE_BUTTON = By.id(EDIT_COURSE_DELETE_BUTTON_ID);
    public static final By NEW_COURSE_MODAL_NAME_FIELD = By.id(NEW_COURSE_MODAL_NAME_FIELD_ID);
    public static final By FORUM_ENTRY_LIST_ENTRY_TITLE = By.className(FORUM_ENTRY_LIST_ENTRY_TITLE_CLASS);
    public static final By FORUM_ENTRY_LIST_ENTRIES_UL = By.className(FORUM_ENTRY_LIST_ENTRIES_UL_CLASS);
    public static final By FORUM_ENTRY_LIST_ENTRY_USER = By.className(FORUM_ENTRY_LIST_ENTRY_USER_CLASS);
    public static final By FORUM_COMMENT_LIST = By.id(FORUM_COMMENT_LIST_ID);
    public static final By FORUM_COMMENT_LIST_ENTRY_TITLE = By.className(FORUM_COMMENT_LIST_ENTRY_TITLE_CLASS);
    public static final By FORUM_COMMENT_LIST_ENTRY_USER = By.className(FORUM_COMMENT_LIST_ENTRY_USER_CLASS);
    public static final By FORUM_COMMENT_LIST_COMMENT = By.className(FORUM_COMMENT_LIST_COMMENT_CLASS);
    public static final By FORUM_COMMENT_LIST_COMMENT_USER = By.className(FORUM_COMMENT_LIST_COMMENT_USER_CLASS);
    public static final By FORUM_COMMENT_LIST_COMMENT_CONTENT = By.className(FORUM_COMMENT_LIST_COMMENT_CONTENT_CLASS);
    public static final By BACK_TO_ENTRIES_LIST_ICON = By.id(FORUM_COMMENT_LIST_BACK_TO_ENTRIES_LIST_ICON_ID);

    public static final By FORUM_NEW_ENTRY_MODAL_TITLE = By.id(FORUM_NEW_ENTRY_MODAL_TITLE_ID);
    public static final By FORUM_NEW_ENTRY_MODAL_CONTENT = By.id(FORUM_NEW_ENTRY_MODAL_CONTENT_ID);
    public static final By FORUM_NEW_COMMENT_MODAL_TEXT_FIELD = By.id(FORUM_NEW_COMMENT_MODAL_TEXT_FIELD_ID);
    public static final By FORUM_NEW_COMMENT_MODAL_POST_BUTTON = By.id(FORUM_NEW_COMMENT_MODAL_POST_BUTTON_ID);
    public static final By FORUM_COMMENT_LIST_MODAL_NEW_REPLY = By.id(FORUM_COMMENT_LIST_MODAL_NEW_REPLY_ID);
    public static final By FORUM_COMMENT_LIST_MODAL_NEW_REPLY_TEXT_FIELD = By.id(FORUM_COMMENT_LIST_MODAL_NEW_REPLY_TEXT_FIELD_ID);
    public static final By FORUM_COMMENT_LIST_COMMENT_DIV = By.className(FORUM_COMMENT_LIST_COMMENT_DIV_CLASS);

    public static final By COURSE_LIST_COURSE_TITLE = By.className(COURSE_LIST_COURSE_TITLE_CLASS);
    public static final By EDIT_COURSE_MODAL_NAME_FIELD = By.id(EDIT_COURSE_MODAL_NAME_FIELD_ID);

    public static final By SESSION_LIST_NEW_SESSION_MODAL_TITLE = By.id(SESSION_LIST_NEW_SESSION_MODAL_TITLE_ID);
    public static final By SESSION_LIST_NEW_SESSION_MODAL_CONTENT = By.id(SESSION_LIST_NEW_SESSION_MODAL_CONTENT_ID);
    public static final By SESSION_LIST_NEW_SESSION_MODAL_DATE = By.id(SESSION_LIST_NEW_SESSION_MODAL_DATE_ID);
    public static final By SESSION_LIST_NEW_SESSION_MODAL_TIME = By.id(SESSION_LIST_NEW_SESSION_MODAL_TIME_ID);
    public static final By SESSION_LIST_SESSION_ROW = By.className(SESSION_LIST_SESSION_ROW_CLASS);
    public static final By SESSION_LIST_SESSION_NAME = By.className(SESSION_LIST_SESSION_NAME_CLASS);
    public static final By SESSION_LIST_EDIT_MODAL_DELETE_DIV = By.className(SESSION_LIST_EDIT_MODAL_DELETE_DIV_CLASS);
    public static final By SESSION_LIST_SESSION_ACCESS = By.className(SESSION_LIST_SESSION_ACCESS_CLASS);

    public static final By ATTENDERS_LIST_ROWS = By.className(ATTENDERS_LIST_ROWS_CLASS);
    public static final By ATTENDERS_LIST_HIGHLIGHTED_ROW = By.className(ATTENDERS_LIST_HIGHLIGHTED_ROW_CLASS);

    //ICONS
    public static final By FORUM_ICON = By.id(FORUM_ICON_ID);
    public static final By HOME_ICON = By.id(HOME_ICON_ID);
    public static final By SESSION_ICON = By.id(SESSION_ICON_ID);
    public static final By FILES_ICON = By.id(FILES_ICON_ID);
    public static final By ATTENDERS_ICON = By.id(ATTENDERS_ICON_ID);

    public static final By FORUM_NEW_ENTRY_ICON = By.id(FORUM_NEW_ENTRY_ICON_ID);
    public static final By FORUM_EDIT_ENTRY_ICON = By.id(FORUM_EDIT_ENTRY_ICON_ID);
    public static final By FORUM_COMMENT_LIST_NEW_COMMENT_ICON = By.className(FORUM_COMMENT_LIST_NEW_COMMENT_ICON_CLASS);
    public static final By FORUM_COMMENT_LIST_COMMENT_REPLY_ICON = By.className(FORUM_COMMENT_LIST_COMMENT_REPLY_ICON_CLASS);

    public static final By SESSION_LIST_NEW_SESSION_ICON = By.className(SESSION_LIST_NEW_SESSION_ICON_CLASS);
    public static final By SESSION_LIST_SESSION_EDIT_ICON = By.className(SESSION_LIST_SESSION_EDIT_ICON_CLASS);

    public static final By SESSION_EXIT_ICON = By.id(SESSION_EXIT_ICON_ID);

    //KEYS
    public static final String SELECT_ALL = Keys.chord(Keys.CONTROL, "a");
    public static final String DELETE = Keys.chord(Keys.BACK_SPACE);
}
