package com.fullteaching.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.session.Session;
import com.google.gson.Gson;

public class SessionTestUtils {

	private static final Logger log = LoggerFactory.getLogger(SessionTestUtils.class);
	private static final String NEW_SESSION_URI ="/api-sessions/course/";
	
	public static Course newSession(MockMvc mvc, Session s, Course c, HttpSession httpSession) {
		Gson gson = new Gson();
		String request = gson.toJson(s);
		Course course = c;
		try {
			MvcResult result =  mvc.perform(post(NEW_SESSION_URI +c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(request)
					                ).andReturn();
			
			String content = result.getResponse().getContentAsString();
			course = CourseTestUtils.json2Course(content);
			
			int status = result.getResponse().getStatus();	
			int expected = HttpStatus.CREATED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);

		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //SessionUtils.newSession");		
		}
	
		return course; 
	}

}
