/**
 * 
 */
package com.fullteaching.backend.integration.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fullteaching.backend.AbstractControllerUnitTest;
import com.fullteaching.backend.utils.LoginTestUtils;

/**
 * @author gtunon
 *
 */
/*@Transactional After each test the BBDD is rolled back*/
@Transactional
class UserControllerTest extends AbstractControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
		
	//urls
	static String newUserUri = "/api-users/new";
	static String changePasswordUri = "/api-users/changePassword";
	static String loginUri = "/api-logIn";
	
	//userStrings
	static String okParameters = "[\"unique@gmail.com\", \"Mock66666\", \"fakeUser\", \"IGNORE\"]";
	static String koParameters1 = "[\"unique@gmail.com\", \"Mock66666\", \"repeatedUser\", \"IGNORE\"]";
	static String koParameters2 = "[\"unique_unique@gmail.com\", \"Mock\", \"InvalidPassword\", \"IGNORE\"]";
	static String koParameters3 = "[\"nonvalidMAIL\", \"Mock66666\", \"fakeUser\", \"IGNORE\"]";
	
	//passParameters
	static String passParameters = "[\"Mock66666\", \"Mock77777\"]";
	static String bad1Parameters = "[\"Mock66666\", \"Mock77777\"]";
	static String bad2Parameters = "[\"Mock77777\", \"notvalid\"]";
	
	//roles
	String[] roles = {"STUDENT"};

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}
	
	/**
	 * Test method for {@link com.fullteaching.backend.user.UserController#newUser(java.lang.String[])}.
	 */
	@Test
	void controllerNewUserTest() {

		/*Test OK*/
		try {
			MvcResult result =  mvc.perform(post(newUserUri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okParameters)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("Exception: newUserTest - OK");
		}
		
		/*Test repeated user*/
		try {
			MvcResult result =  mvc.perform(post(newUserUri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(koParameters1)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CONFLICT.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("Exception: newUserTest - repeated user");

		}
		
		/*Test bad password*/
		try {
			MvcResult result =  mvc.perform(post(newUserUri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(koParameters2)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
				
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("Exception: newUserTest - badPassword");

		}	
		
		/*Test bad email*/
		try {
			MvcResult result =  mvc.perform(post(newUserUri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(koParameters3)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.FORBIDDEN.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("Exception: newUserTest - badEmail");

		}
	}

	/**
	 * Test method for {@link com.fullteaching.backend.user.UserController#changePassword(java.lang.String[])}.
	 * @throws Exception 
	 */
	@Test
	void userChangePasswordTest() throws Exception {
	
			/*Create new user*/
			LoginTestUtils.registerUserIfNotExists(mvc, okParameters);
			
			/*Login user*/
			HttpSession session = LoginTestUtils.logIn(mvc, "unique@gmail.com", "Mock66666");
			
			try {
			/*Test change password OK*/
			MvcResult resultPass = mvc.perform(put(changePasswordUri)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(passParameters)
					.session((MockHttpSession) session)
				).andReturn();
			
			int statusPass = resultPass.getResponse().getStatus();
                assertEquals(statusPass, HttpStatus.OK.value(), "failure login - expected HTTP status " +
                        HttpStatus.OK.value() +
                        " but was: " + statusPass);
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("Exception: newUserTest - OK");

		}
		try {
			/*Test change password bad initial password*/
			MvcResult resultBad1 = mvc.perform(put(changePasswordUri)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(bad1Parameters)
					.session((MockHttpSession) session)
				).andReturn();
			
			int statusBad1 = resultBad1.getResponse().getStatus();
            assertEquals(statusBad1, HttpStatus.CONFLICT.value(), "failure login - expected HTTP status " +
                    HttpStatus.CONFLICT.value() +
                    " but was: " + statusBad1);
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("Exception: newUserTest - OK");

		}
		try {	
			/*Test change password bad initial password*/
			MvcResult resultBad2 = mvc.perform(put(changePasswordUri)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(bad2Parameters)
					.session((MockHttpSession) session)
				).andReturn();
			
			int statusBad2 = resultBad2.getResponse().getStatus();
            assertEquals(statusBad2, HttpStatus.NOT_MODIFIED.value(), "failure login - expected HTTP status " +
                    HttpStatus.NOT_MODIFIED.value() +
                    " but was: " + statusBad2);
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("Exception: newUserTest - OK");
		}
	
	}
	
	

}
