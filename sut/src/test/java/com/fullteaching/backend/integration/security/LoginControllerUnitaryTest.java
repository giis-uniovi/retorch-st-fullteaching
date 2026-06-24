package com.fullteaching.backend.integration.security;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fullteaching.backend.AbstractControllerUnitTest;
import com.fullteaching.backend.utils.LoginTestUtils;

class LoginControllerUnitaryTest extends AbstractControllerUnitTest {

	
	String userParameters = "[\"fakeemail@gmail.com\", \"Mock66666\", \"fakeUser\", \"IGNORE\"]";

	String logoutUri = "/api-logOut";
	String loginUri = "/api-logIn";

	@Override
	@BeforeEach
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webAppCtx)
				.apply(springSecurity())
				.build();
	}

	@Test
	void logInSecurityTest() throws Exception {

		LoginTestUtils.registerUserIfNotExists(mvc, userParameters);
		
		String userPass = "fakeemail@gmail.com:Mock66666";

		MvcResult resultLogin = mvc.perform(get(loginUri)
						.header("Authorization", "Basic "+LoginTestUtils.utf8_to_b64(userPass))
						.header("X-Requested-With", "XMLHttpRequest")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
				).andReturn();
		
		System.out.println(resultLogin.getResponse());
		int statusLogin = resultLogin.getResponse().getStatus();

        assertEquals(statusLogin, HttpStatus.OK.value(), "failure login - expected HTTP status " +
                HttpStatus.OK.value() +
                " but was: " + statusLogin);
		
		//login KO 
		MvcResult resultLoginKo = mvc.perform(get(loginUri)
						.header("X-Requested-With", "XMLHttpRequest")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
				).andReturn();
		
		System.out.println(resultLoginKo.getResponse());
		int statusLoginKo = resultLoginKo.getResponse().getStatus();

        assertEquals(statusLoginKo, HttpStatus.UNAUTHORIZED.value(), "failure login - expected HTTP status " +
                HttpStatus.UNAUTHORIZED.value() +
                " but was: " + statusLoginKo);

		//login KO bad password
		String userBadPass = "fakeemail@gmail.com:BadPass";

		MvcResult resultLoginBadPassword = mvc.perform(get(loginUri)
						.header("Authorization", "Basic "+LoginTestUtils.utf8_to_b64(userBadPass))
						.header("X-Requested-With", "XMLHttpRequest")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
				).andReturn();
		
		System.out.println(resultLoginKo.getResponse());
		int statusLoginBadPassword = resultLoginBadPassword.getResponse().getStatus();

        assertEquals(statusLoginBadPassword, HttpStatus.UNAUTHORIZED.value(), "failure login - expected HTTP status " +
                HttpStatus.UNAUTHORIZED.value() +
                " but was: " + statusLoginBadPassword);
		
		//login KO nouser
				String noUser = "nouser:BadPass";

				MvcResult resultLoginNoUser = mvc.perform(get(loginUri)
								.header("Authorization", "Basic "+LoginTestUtils.utf8_to_b64(noUser))
								.header("X-Requested-With", "XMLHttpRequest")
							.contentType(MediaType.APPLICATION_JSON_VALUE)
						).andReturn();
				
				System.out.println(resultLoginKo.getResponse());
				int statusLoginNoUser = resultLoginNoUser.getResponse().getStatus();

        assertEquals(statusLoginNoUser, HttpStatus.UNAUTHORIZED.value(), "failure login - expected HTTP status " +
                HttpStatus.UNAUTHORIZED.value() +
                " but was: " + statusLoginNoUser);
		
		
	}

	@Test
	void logOutSecurityTest() throws Exception {
		/*Create new user*/
		LoginTestUtils.registerUserIfNotExists(mvc, userParameters);
		
		/*Login user*/
		HttpSession session = LoginTestUtils.logIn(mvc, "fakeemail@gmail.com", "Mock66666");
		
		/*Test LogOut OK*/
		MvcResult resultPass = mvc.perform(get(logoutUri)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.session((MockHttpSession) session)
			).andReturn();
		
		int statusPass = resultPass.getResponse().getStatus();
        assertEquals(statusPass, HttpStatus.OK.value(), "failure login - expected HTTP status " +
                HttpStatus.OK.value() +
                " but was: " + statusPass);
		
		/*Test LogOut Unauthorized*/
		MvcResult resultUnauthorized = mvc.perform(get(logoutUri)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.session((MockHttpSession) session)
			).andReturn();
		
		int statusUnauthorized = resultUnauthorized.getResponse().getStatus();
        assertEquals(statusUnauthorized, HttpStatus.UNAUTHORIZED.value(), "failure login - expected HTTP status " +
                HttpStatus.UNAUTHORIZED.value() +
                " but was: " + statusUnauthorized);
		
		
	
	}

}
