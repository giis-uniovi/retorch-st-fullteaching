package com.fullteaching.backend.integration.entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import com.fullteaching.backend.AbstractLoggedControllerUnitTest;
import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.utils.CourseTestUtils;
import com.google.gson.Gson;

class EntryControllerTest extends AbstractLoggedControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(EntryControllerTest.class);
	
	
	private static String newEntryUri ="/api-entries/forum/";
	
	@BeforeEach
	@Override
	public void setUp() {
		super.setUp();
	}

	@Test
	void newForumEntryControllerTest() {
		
		Course c = CourseTestUtils.newCourseWithCd("Test Forum", loggedUser, null, "this is the info", true);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);

		long forumId = c.getCourseDetails().getForum().getId();
		long cdId = c.getCourseDetails().getId();
		
		
		Comment cm = new Comment("This is the message", System.currentTimeMillis(), loggedUser);
		Entry entry = new Entry("Test Entry",System.currentTimeMillis(),loggedUser);
		entry.getComments().add(cm);
		
		assertTrue((forumId>-1)&&(cdId>-1));
		
		Gson gson = new Gson();
		String entryRequest = gson.toJson(entry);
		
		//test ok 
		try {
			
			MvcResult result =  mvc.perform(post(newEntryUri +forumId)//fakeID
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(entryRequest)
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
			
			MvcResult result =  mvc.perform(post(newEntryUri +forumId)//fakeID
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(entryRequest)
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
			
			MvcResult result =  mvc.perform(post(newEntryUri +"not_a_id")//fakeID
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
