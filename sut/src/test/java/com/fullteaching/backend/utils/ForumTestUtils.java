package com.fullteaching.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.forum.Forum;
import com.google.gson.Gson;

public class ForumTestUtils {

	private static final Logger log = LoggerFactory.getLogger(ForumTestUtils.class);
	private static final String NEW_ENTRY_URI ="/api-entries/forum/";

	public static Course newEntry(MockMvc mvc, Course c, Entry e, HttpSession httpSession) {
		
		Gson gson = new Gson();
		String entryRequest = gson.toJson(e);
		
		long cdId = c.getCourseDetails().getId();
		
		try {
			
			MvcResult result =  mvc.perform(post(NEW_ENTRY_URI +cdId)//fakeID
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(entryRequest)
					                ).andReturn();
			
			
			String content = result.getResponse().getContentAsString();
			int status = result.getResponse().getStatus();
			
			JSONObject json = (JSONObject) new JSONParser().parse(content);
			json = (JSONObject) json.get("entry");
			
			Entry entry = json2Entry(json.toJSONString());
			
			c.getCourseDetails().getForum().setEntries(new ArrayList<>());
			c.getCourseDetails().getForum().getEntries().add(entry);
			
			int expected = HttpStatus.CREATED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception ex) {
			log.error("Unexpected exception during test execution", ex);
			fail("EXCEPTION: ForumUtils.newEntry");
		}
		
		
		return c;
	}
	
	public static Entry json2Entry(String json) throws IOException {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, Entry.class);
	}
	
	public static Forum json2Forum(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, Forum.class);
	}

}
