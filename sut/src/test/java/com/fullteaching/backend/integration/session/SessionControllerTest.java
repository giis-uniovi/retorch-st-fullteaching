package com.fullteaching.backend.integration.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import com.fullteaching.backend.AbstractLoggedControllerUnitTest;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.utils.CourseTestUtils;
import com.fullteaching.backend.utils.SessionTestUtils;
import com.google.gson.Gson;

class SessionControllerTest extends AbstractLoggedControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(SessionControllerTest.class);
	
	private static final String NEW_SESSION_URI ="/api-sessions/course/";
	private static final String EDIT_SESSION_URI ="/api-sessions/edit";
	private static final String DELETE_SESSION_URI ="/api-sessions/delete/";

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}

	@Test
	void newSessionTest() {
		
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "This is the info", false);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);

		long date = System.currentTimeMillis();
		Session s = new Session("Mock Session", "this descriptions", date, c);
		
		Gson gson = new Gson();
		String request = gson.toJson(s);

		try {
			MvcResult result =  mvc.perform(post(NEW_SESSION_URI +c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(request)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//test UNAUTHORIZED 
		try {
			
			MvcResult result =  mvc.perform(post(NEW_SESSION_URI +c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(request)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test UNAUTHORIZED");
		}
		
		//test BAD_REQUEST 
		try {
			
			MvcResult result =  mvc.perform(post(NEW_SESSION_URI +"not_a_id")
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test BAD_REQUEST");
		}
		
	}

	@Test
	void modifySessionTest() {
		
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "This is the info", false);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);

		long date = System.currentTimeMillis();
		Session s = new Session("Mock Session", "this descriptions", date, c);
		
		c = SessionTestUtils.newSession(mvc, s, c, httpSession);
		
		Session toChange = (Session)c.getSessions().toArray()[0];
		
		toChange.setDate(System.currentTimeMillis());
		toChange.setTitle("MODIFIED");
		Gson gson = new Gson();
		
		String request = gson.toJson(toChange);

		try {
			MvcResult result =  mvc.perform(put(EDIT_SESSION_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(request)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//test UNAUTHORIZED 
		try {
			
			MvcResult result =  mvc.perform(put(EDIT_SESSION_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(request)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test UNAUTHORIZED");
		}
		
		//test BAD_REQUEST 
		try {
			
			MvcResult result =  mvc.perform(put(EDIT_SESSION_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test BAD_REQUEST");
		}
	}

	@Test
	void deleteSessionTest() {
		
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "This is the info", false);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);

		long date = System.currentTimeMillis();
		Session s = new Session("Mock Session", "this descriptions", date, c);
		
		c = SessionTestUtils.newSession(mvc, s, c, httpSession);
		
		long sessionId = ((Session)c.getSessions().toArray()[0]).getId();


		try {
			MvcResult result =  mvc.perform(delete(DELETE_SESSION_URI +sessionId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//test UNAUTHORIZED 
		try {
			
			MvcResult result =  mvc.perform(delete(DELETE_SESSION_URI +sessionId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test UNAUTHORIZED");
		}
		
		//test BAD_REQUEST 
		try {
			
			MvcResult result =  mvc.perform(delete(DELETE_SESSION_URI +"not_a_id")
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test BAD_REQUEST");
		}
		//Course ==null hasn't been found...
	}

}
