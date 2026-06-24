package com.fullteaching.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullteaching.backend.utils.LoginTestUtils;
import com.fullteaching.backend.user.User;

@WebAppConfiguration
public abstract class AbstractLoggedControllerUnitTest extends AbstractControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractLoggedControllerUnitTest.class);

	
	protected HttpSession httpSession; 
	
	protected User loggedUser;

	@BeforeEach
	@Override
	public void setUp() {
		String userParameters = "[\"fakeemail2@gmail.com\", \"Mock66666\", \"fakeUser\", \"IGNORE\"]";
		
		super.setUp();
		
		try {
			if (httpSession == null) {
			
				loggedUser = LoginTestUtils.registerUserIfNotExists(mvc, userParameters);
				
				httpSession = LoginTestUtils.logIn(mvc, "fakeemail2@gmail.com", "Mock66666", loggedUser);
				
				if (loggedUser == null)
					loggedUser = (User)httpSession.getAttribute("loggedUser");
			}
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
		}	
	}
		
	@Override
	protected String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	@Override
	protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, clazz);
	}
}
