package com.fullteaching.backend.integration.comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;

import com.fullteaching.backend.AbstractLoggedControllerUnitTest;
import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.utils.CourseTestUtils;
import com.fullteaching.backend.utils.ForumTestUtils;
import com.google.gson.Gson;

class CommentControllerTest extends AbstractLoggedControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(CommentControllerTest.class);

	private static String newCommentUri ="/api-comments/entry/{entryId}/forum/";
	
	private static String courseTitle = "Course Title";
	private static String info ="Course information";
	private static boolean forum = true;
	
	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
		
	}

	@Rollback
	@Test
	void newCommentTest() {
		
	
		Course c = CourseTestUtils.newCourseWithCd(courseTitle, loggedUser, null, info, forum);	
			
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
				
		Comment cm = new Comment("This is the message", System.currentTimeMillis(), loggedUser);
		Entry entry = new Entry("Test Entry",System.currentTimeMillis(),loggedUser);
		entry.getComments().add(cm);	
		
		c = ForumTestUtils.newEntry(mvc, c, entry, httpSession);
		
		long entryId = c.getCourseDetails().getForum().getEntries().get(0).getId();
		long forumId = c.getCourseDetails().getForum().getId();
		
		Comment comment = new Comment();
		comment.setMessage("New Comment");
		
		Gson gson = new Gson();
		String requestOK = gson.toJson(comment);
		
		//test new message
		//test ok 
		try {
			
			MvcResult result =  mvc.perform(post(newCommentUri.replace("{entryId}", String.valueOf(entryId))+forumId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestOK)
					                ).andReturn();
			
			String content = result.getResponse().getContentAsString();
			
			JSONObject json = (JSONObject) new JSONParser().parse(content);
			json = (JSONObject) json.get("entry");
			
			Entry e = ForumTestUtils.json2Entry(json.toJSONString());
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			assertEquals(loggedUser, e.getComments().get(0).getUser(), "failure - expected user x");

		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//test UNAUTHORIZED 
		try {
			
			MvcResult result =  mvc.perform(post(newCommentUri.replace("{entryId}", String.valueOf(entryId))+forumId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(requestOK)
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
			
			MvcResult result =  mvc.perform(post(newCommentUri.replace("{entryId}", String.valueOf(entryId))+"not_a_id")
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
	
	@Rollback
	@Test
	void replyCommentTest() throws Exception {
		
		Course c = CourseTestUtils.newCourseWithCd(courseTitle, loggedUser, null, info, forum);	
		
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
						
		Comment cm = new Comment("This is the message", System.currentTimeMillis(), loggedUser);
		Entry entry = new Entry("Test Entry",System.currentTimeMillis(),loggedUser);
		entry.getComments().add(cm);		
		c = ForumTestUtils.newEntry(mvc, c, entry, httpSession);
		
		long entryId = c.getCourseDetails().getForum().getEntries().get(0).getId();
		long forumId = c.getCourseDetails().getForum().getId();
		
		Comment parent = c.getCourseDetails().getForum().getEntries().get(0).getComments().get(0);
		Comment comment = new Comment();
		comment.setMessage("New Comment");
		comment.setCommentParent(parent);
		
		Gson gson = new Gson();
		String requestOK = gson.toJson(comment);
		
		//test new message
		//test ok 
		try {
			
			MvcResult result =  mvc.perform(post(newCommentUri.replace("{entryId}", String.valueOf(entryId))+forumId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestOK)
					                ).andReturn();
			
			String content = result.getResponse().getContentAsString();
			
			JSONObject json = (JSONObject) new JSONParser().parse(content);
			json = (JSONObject) json.get("entry");
			
			Entry e = ForumTestUtils.json2Entry(json.toJSONString());

			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			assertEquals(loggedUser, e.getComments().get(0).getReplies().get(0).getUser(), "failure - expected user x");
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//test UNAUTHORIZED 
		try {
			
			MvcResult result =  mvc.perform(post(newCommentUri.replace("{entryId}", String.valueOf(entryId))+forumId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(requestOK)
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
			
			MvcResult result =  mvc.perform(post(newCommentUri.replace("{entryId}", "not_anID")+"not_a_id")
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
}
