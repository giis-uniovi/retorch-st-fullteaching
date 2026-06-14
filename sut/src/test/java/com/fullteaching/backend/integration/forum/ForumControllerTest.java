package com.fullteaching.backend.integration.forum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
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
import com.fullteaching.backend.utils.CourseTestUtils;

class ForumControllerTest extends AbstractLoggedControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(ForumControllerTest.class);
	
	private static final String TOGGLE_FORUM_URI = "/api-forum/edit/";

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}

	@Test
	void toggleForumTest() {
		
		Course c = CourseTestUtils.newCourseWithCd("Course Title", loggedUser, null, "this is the info", false);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);

		//test ok 
		try {
			MvcResult result =  mvc.perform(put(TOGGLE_FORUM_URI +c.getCourseDetails().getId())//fakeID
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content("true")
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
			
			MvcResult result =  mvc.perform(put(TOGGLE_FORUM_URI +c.getCourseDetails().getId())//fakeID
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content("true")
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
			
			MvcResult result =  mvc.perform(put(TOGGLE_FORUM_URI +"not_a_id")//fakeID
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

}
