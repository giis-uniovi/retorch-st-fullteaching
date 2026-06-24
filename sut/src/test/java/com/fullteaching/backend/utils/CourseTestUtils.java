package com.fullteaching.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.user.User;
import com.google.gson.Gson;

public class CourseTestUtils {

	private static final Logger log = LoggerFactory.getLogger(CourseTestUtils.class);
	
	private static String newCourseUri = "/api-courses/new";
	private static String addAttendersUri = "/api-courses/edit/add-attenders/course/";


	public static Course createCourseIfNotExist(MockMvc mvc, Course c, HttpSession httpSession) {
		String okRequest ="{\"title\":\"TEST COURSE\"," +
				"\"teacher\":null, "+
				"\"image\":\"/../assets/images/default_session_image.png\","+
				"\"courseDetails\":{\"info\":\"\","+
				 					"\"forum\":{\"activated\":true," +
				 								"\"entries\":[]}," +
				 					 "\"files\":[]}," + 
				"\"sessions\":[]," + 
				"\"attenders\":[]}" ;

		try {
			okRequest = course2JsonStr(c);
		}
		
		catch(NullPointerException e) {
			//nothing to do
		}
		//test OK
		try {
			//there is no courses so how to mock that?
			MvcResult result =  mvc.perform(post(newCourseUri)//fakeID
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okRequest)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
			
			String content = result.getResponse().getContentAsString();
			return json2Course(content);
				
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
		}
		return null;
	}


	public static Course addAttenders(MockMvc mvc, HttpSession httpSession, Course c, String[][] attendantsStrings) throws Exception {
		
		StringBuilder attendersEmails = new StringBuilder("[");
        for (String[] attendantsString : attendantsStrings) {
            attendersEmails.append("\"").append(attendantsString[0]).append("\",");
            LoginTestUtils.registerUserIfNotExists(mvc, "[\"" + attendantsString[0] + "\","
                    + "\"" + attendantsString[1] + "\","
                    + "\"" + attendantsString[2] + "\","
                    + "\"" + attendantsString[3] + "\"]");
        }
		attendersEmails = new StringBuilder(attendersEmails.substring(0, attendersEmails.length() - 1) + "]");

		try {
			//there is no courses so how to mock that?
			MvcResult result =  mvc.perform(put(addAttendersUri +c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(attendersEmails.toString())
					                ).andReturn();
			
			String content = result.getResponse().getContentAsString();
			JSONObject  json = new JSONObject(content);		
			int status = result.getResponse().getStatus();	
			ObjectMapper mapper = new ObjectMapper();
			Set<User> users = new HashSet<User>();
			JSONArray aux = (JSONArray) json.get("attendersAdded");
			for(int i=0; i < aux.length(); i++) {
				JSONObject o = aux.getJSONObject(i);
				users.add(mapper.readValue(o.toString(), User.class));
			}
			aux = (JSONArray) json.get("attendersAlreadyAdded");
			for(int i=0; i < aux.length(); i++) {
				JSONObject o = aux.getJSONObject(i);
				users.add(mapper.readValue(o.toString(), User.class));
			}
			c.setAttenders(users);
			int expected = HttpStatus.OK.value();
			//http status 200 created!
			assertEquals(expected, status, "failure CourseUtils.addAttenders  - expected HTTP status "+expected);
			
		} catch (Exception e) {
		log.error("Unexpected exception during test execution", e);
		}
		
		return c;
	}
	
	
	public static String course2JsonStr(Course c) throws NullPointerException {
		if(c!=null) {
			Gson gson = new Gson();
			return gson.toJson(c);
		}
		else {
			throw new NullPointerException("Course is Null");
		}
	}
	
	
	public static Course json2Course(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, Course.class);
	}
	
	public static CourseDetails json2CourseDetails(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		json = json.replaceAll("\"" + "fileExtension" + "\"[ ]*:[^,}\\]]*[,]?", "");
		json = json.replaceAll(",}","}");
		return mapper.readValue(json, CourseDetails.class);
	}


	public static Course newCourse (String courseTitle, User loggedUser, Set<User> attendants) {
		Course c = new Course(courseTitle, "/../assets/images/default_session_image.png", loggedUser);
		
		if(attendants != null) c.setAttenders(attendants);
		
		return c;
	}
	
	public static Course newCourseWithCd(String courseTitle, User loggedUser, Set<User> attendants, String info,
			boolean forum) {
		
		Course c = newCourse(courseTitle, loggedUser, attendants);
		CourseDetails cd = new CourseDetails();
		
		if(info != null) cd.setInfo(info);
		cd.getForum().setActivated(forum);
		
		c.setCourseDetails(cd);	
		
		return c;
	}
	
	
}
