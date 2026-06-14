package com.fullteaching.backend.integration.course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.HashSet;
import java.util.Set;

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
import com.fullteaching.backend.utils.LoginTestUtils;
import com.fullteaching.backend.user.User;

class CourseControllerTest extends AbstractLoggedControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(CourseControllerTest.class);


	private static final String GET_COURSES_URI = "/api-courses/user/";
	private static final String GET_COURSE_URI = "/api-courses/course/";
	private static final String NEW_COURSE_URI = "/api-courses/new";
	private static final String EDIT_COURSE_URI = "/api-courses/edit";
	private static final String DELETE_COURSE_URI = "/api-courses/delete/";
	private static final String ADD_ATTENDERS_URI = "/api-courses/edit/add-attenders/course/";
	private static final String DELETE_ATTENDERS_URI = "/api-courses/edit/delete-attenders";
	
	private static final String[][] attendantsStrings = {	{"fakeemail2@gmail.com","Mock66666","fakeUser","IGNORE"},
													{"fakeemail1@gmail.com","Mock66666","fakeUser","IGNORE"}};
	private static final String[][] secondAtemptAttendant = {	{"invalidEmail","Mock66666","fakeUser","IGNORE"},
														{"fakeemail2@gmail.com","repeated","fakeUser","IGNORE"},
														{"ok@gmail.com","OKUser1234","fakeUser","IGNORE"}};
	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}

	@Test
	void getCoursesFromUserTest() {
		//test OK
		try {

			MvcResult result =  mvc.perform(get(GET_COURSES_URI +"741")
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
		
		//test unauthorized
		try {

			MvcResult result =  mvc.perform(get(GET_COURSES_URI +"741")
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test unauthorized");
		}
		
		//KO no long id
		try {
			
			MvcResult result =  mvc.perform(get(GET_COURSES_URI +"no_long")
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();

			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //KO no long id");
		}
		
	}

	@Test
	void getCourseByIdTest() {
		//test OK
				try {
					
					MvcResult result =  mvc.perform(get(GET_COURSE_URI +"741")
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
				//test unauthorized
				try {
					
					MvcResult result =  mvc.perform(get(GET_COURSE_URI +"741")
							                .contentType(MediaType.APPLICATION_JSON_VALUE)
							                ).andReturn();
				
					int status = result.getResponse().getStatus();
					
					int expected = HttpStatus.UNAUTHORIZED.value();
					
					assertEquals(expected, status, "failure - expected HTTP status "+expected);
				
				} catch (Exception e) {
					log.error("Unexpected exception during test execution", e);
					fail("EXCEPTION: //KO unauthorized");
				}
				//KO no long id
				try {
					MvcResult result =  mvc.perform(get(GET_COURSE_URI +"no_long")
							                .contentType(MediaType.APPLICATION_JSON_VALUE)
							                .session((MockHttpSession) httpSession)
							                ).andReturn();
				
					int status = result.getResponse().getStatus();
					
					int expected = HttpStatus.BAD_REQUEST.value();

					assertEquals(expected, status, "failure - expected HTTP status "+expected);
				
				} catch (Exception e) {
					log.error("Unexpected exception during test execution", e);
					fail("EXCEPTION: //test KO no long id");
				}
	}

	@Test
	void newCourseTest() {
		
		Course c = CourseTestUtils.newCourseWithCd("Test Course", loggedUser, null, "empty", true);
		Course c2= CourseTestUtils.newCourse("Test Course", loggedUser, null);		
		
		String okRequest =CourseTestUtils.course2JsonStr(c);
		String okRequestNullCd = CourseTestUtils.course2JsonStr(c2);
		
		
		//test OK
		try {
			MvcResult result =  mvc.perform(post(NEW_COURSE_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okRequest)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//test OK cd
		try {
			MvcResult result =  mvc.perform(post(NEW_COURSE_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okRequestNullCd)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK cd");
		}
		//test unauthorized
		try {
			MvcResult result =  mvc.perform(post(NEW_COURSE_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okRequest)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test unauthorized");
		}
		
		//test bad request
		try {
			MvcResult result =  mvc.perform(post(NEW_COURSE_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
		
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test  bad request");
		}
		
	}

	@Test
	void modifyCourseTest() {
		
		Course c = CourseTestUtils.newCourse("To Modify", loggedUser, null);
		Course c2 = CourseTestUtils.newCourse("Modified", null, null);
		
		Course cwcd = CourseTestUtils.newCourseWithCd("To Modify", loggedUser, null, "info", false);
		Course cwcd2 = CourseTestUtils.newCourseWithCd("To Modify", loggedUser, null, "Modified", false);

		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession); 
		c2.setId(c.getId());
		
		cwcd = CourseTestUtils.createCourseIfNotExist(mvc, cwcd, httpSession);
        assertNotNull(cwcd);
        cwcd2.setId(cwcd.getId());
		
		String okRequest =CourseTestUtils.course2JsonStr(c2);
		String okRequest2 = CourseTestUtils.course2JsonStr(cwcd2);

		try {
			MvcResult result =  mvc.perform(put(EDIT_COURSE_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okRequest.replaceAll("_ID_", String.valueOf(c.getId())))
					                .session((MockHttpSession) httpSession)
					                ).andReturn();

			
			int status = result.getResponse().getStatus();
			int expected = HttpStatus.OK.value();
			
			String content = result.getResponse().getContentAsString();
			Course cRes = CourseTestUtils.json2Course(content);

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			assertEquals(c2.getTitle(), cRes.getTitle(), "failure - expected title: "+c2.getTitle());
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		
		try {
			MvcResult result =  mvc.perform(put(EDIT_COURSE_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okRequest2.replaceAll("_ID_", String.valueOf(c.getId())))
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
			

			int status = result.getResponse().getStatus();
			int expected = HttpStatus.OK.value();
			
			String content = result.getResponse().getContentAsString();
			Course cRes = CourseTestUtils.json2Course(content);

			assertEquals(expected, status, "failure - expected HTTP status (2) "+expected);
			assertEquals(cwcd2.getCourseDetails().getInfo(), cRes.getCourseDetails().getInfo(), "failure - expected title(2): "+cwcd2.getCourseDetails().getInfo());
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//test unauthorized
		try {
			MvcResult result =  mvc.perform(put(EDIT_COURSE_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(okRequest.replaceAll("_ID_", String.valueOf(c.getId())))
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test unauthorized");
		}
			
	}

	@Test
	void delteteCourseTest() {
		
		Course c = CourseTestUtils.newCourse("to delete", loggedUser, null);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
		
		//test unauthorized
		try {
			MvcResult result =  mvc.perform(delete(DELETE_COURSE_URI +c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test unauthorized");
		}
		
		//test OK request
		try {
			MvcResult result =  mvc.perform(delete(DELETE_COURSE_URI +"not_a_course")
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
		
		try {
				MvcResult result =  mvc.perform(delete(DELETE_COURSE_URI +c.getId())
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
			
	}

	@Test
	void addAttenders2CourseTest() throws Exception {
		
		//Prepare Test

		StringBuilder attendersEmails = new StringBuilder("[");
        for (String[] attendantsString : attendantsStrings) {
            attendersEmails.append("\"").append(attendantsString[0]).append("\",");
            LoginTestUtils.registerUserIfNotExists(mvc, "[\"" + attendantsString[0] + "\","
                    + "\"" + attendantsString[1] + "\","
                    + "\"" + attendantsString[2] + "\","
                    + "\"" + attendantsString[3] + "\"]");
        }
		attendersEmails = new StringBuilder(attendersEmails.substring(0, attendersEmails.length() - 1) + "]");
		
		StringBuilder attenders2Emails = new StringBuilder("[");
        for (String[] strings : secondAtemptAttendant) {
            attenders2Emails.append("\"").append(strings[0]).append("\",");
            LoginTestUtils.registerUserIfNotExists(mvc, "[\"" + strings[0] + "\","
                    + "\"" + strings[1] + "\","
                    + "\"" + strings[2] + "\","
                    + "\"" + strings[3] + "\"]");
        }
		attenders2Emails = new StringBuilder(attenders2Emails.substring(0, attenders2Emails.length() - 1) + "]");
		
		Course c = CourseTestUtils.newCourse("to modify", loggedUser, null);

		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
		
		//test unauthorized
		try {
				MvcResult result =  mvc.perform(put(ADD_ATTENDERS_URI +c.getId())
						                .contentType(MediaType.APPLICATION_JSON_VALUE)
						                .content(attendersEmails.toString())
						                ).andReturn();
				
				int status = result.getResponse().getStatus();
				
				int expected = HttpStatus.UNAUTHORIZED.value();

				assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
			} catch (Exception e) {
				log.error("Unexpected exception during test execution", e);
				fail("EXCEPTION: //test UNAUTHORIZED");
			}
		//test bad request
		try {
			MvcResult result =  mvc.perform(put(ADD_ATTENDERS_URI +"not_a_course")
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
		
		//test ok 1
		try {
			MvcResult result =  mvc.perform(put(ADD_ATTENDERS_URI +c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(attendersEmails.toString())
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		
		//test ok 2
		try {
			MvcResult result =  mvc.perform(put(ADD_ATTENDERS_URI +c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(attenders2Emails.toString())
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();
			
			assertEquals(expected, status, "failure - expected HTTP status (2) "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		
	}

	@Test
	void deleteAttenderFromCourseTest() throws Exception {
		
		Course c = CourseTestUtils.newCourse("to modify", loggedUser, null);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
		c = CourseTestUtils.addAttenders(mvc,httpSession,c,attendantsStrings);
		
		Set<User> cattenders = new HashSet<User>();
		cattenders.add((User)c.getAttenders().toArray()[0]);
		
		Course courseCopy = CourseTestUtils.newCourse(c.getTitle(), loggedUser, cattenders);
		courseCopy.setId(c.getId());
				
		String okRequest =  CourseTestUtils.course2JsonStr(courseCopy);
		
		//test unauthorized
		try {
				MvcResult result =  mvc.perform(put(DELETE_ATTENDERS_URI)
						                .contentType(MediaType.APPLICATION_JSON_VALUE)
						                .content(okRequest)
						                ).andReturn();
				
				int status = result.getResponse().getStatus();
				
				int expected = HttpStatus.UNAUTHORIZED.value();

				assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
			} catch (Exception e) {
				log.error("Unexpected exception during test execution", e);
				fail("EXCEPTION: //test UNAUTHORIZED");
			}
		//test bad request
		try {
			MvcResult result =  mvc.perform(put(DELETE_ATTENDERS_URI)
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
		
		//test ok 
		try {
			MvcResult result =  mvc.perform(put(DELETE_ATTENDERS_URI)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(okRequest)
					                ).andReturn();
			
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();

			assertEquals(expected, status, "failure - expected HTTP status  "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
	}

}
