package com.fullteaching.backend.integration.filegroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import com.fullteaching.backend.AbstractLoggedControllerUnitTest;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.file.File;
import com.fullteaching.backend.filegroup.FileGroup;
import com.fullteaching.backend.utils.CourseTestUtils;
import com.fullteaching.backend.utils.FileTestUtils;

class FileGroupControllerTest extends AbstractLoggedControllerUnitTest {

	private static final Logger log = LoggerFactory.getLogger(FileGroupControllerTest.class);

	private static final String NEW_FILE_URI ="/api-files/";
	private static final String MODIFY_GROUP_FILE_URI ="/api-files/edit/file-group/course/";
	private static final String EDIT_ORDER_URI ="/api-files/edit/file-order/course/{courseId}/file/{fileId}/from/{sourceID}/to/{targetId}/pos/";//newPosition
	private static final String MODIFY_FILE_URI ="/api-files/edit/file/file-group/{fileGroupId}/course/";
	private static final String DELETE_GROUP_URI ="/api-files/delete/file-group/{fileGroupId}/course/";
	private static final String DELETE_FILE_URI ="/api-files/delete/file/{fileId}/file-group/{fileGroupId}/course/";

	@BeforeEach
	@Override
	public void setUp() {
		super.setUp();
	}

	@Test
	void testNewFileGroup() {
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "this is the info", true);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
		
		CourseDetails cd = null; 
		
		FileGroup fg = new FileGroup("New FileGroup");
		String requestOK = FileTestUtils.fileGroup2Json(fg);
		
		long courseId = c.getCourseDetails().getId();
		
		
		try {

			MvcResult result =  mvc.perform(post(NEW_FILE_URI +courseId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestOK)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();

			String content = result.getResponse().getContentAsString();
			cd = CourseTestUtils.json2CourseDetails(content);
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		
		//Unauthorized
		try {

			MvcResult result =  mvc.perform(post(NEW_FILE_URI +courseId)
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
		

		//BAD_REQUEST
		try {

			MvcResult result =  mvc.perform(post(NEW_FILE_URI +"notANumber")
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
		
		
		//Test for filegroups with parent
		fg = FileTestUtils.getFileGroupFromCd(cd, fg.getTitle());
		FileGroup fg2 = new FileGroup("New FileGroup with parent", fg);
		String requestWithParent = FileTestUtils.fileGroup2Json(fg2);
		
		try {

			MvcResult result =  mvc.perform(post(NEW_FILE_URI +courseId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestWithParent)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();
		
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK with Parent");
		}
		
		//fake parent
		fg.setId(5654);
		FileGroup fg3 = new FileGroup("New FileGroup with parent", fg);
		String requestWithParentBadRequest = FileTestUtils.fileGroup2Json(fg3);
		
		try {

			MvcResult result =  mvc.perform(post(NEW_FILE_URI +courseId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestWithParentBadRequest)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();
		
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK with Parent");
		}
		
	}

	@Test
	void testModifyFileGroup() {
		//Prepare Test
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "this is the info", true);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
		
		FileGroup fg = new FileGroup("New FileGroup");
		fg = FileTestUtils.newFileGroup(mvc, httpSession, fg, c);
		long courseId = c.getId();

        assertNotNull(fg);
        fg.setTitle("Modified FileGroup");
		
		String requestOk = FileTestUtils.fileGroup2Json(fg);
		try {

			MvcResult result =  mvc.perform(put(MODIFY_GROUP_FILE_URI +courseId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestOk)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();

			FileGroup fg1 = FileTestUtils.json2FileGroup(result.getResponse().getContentAsString());
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			assertEquals("Modified FileGroup", fg1.getTitle(), "not modified");
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		
		//Unauthorized
		try {

			MvcResult result =  mvc.perform(put(MODIFY_GROUP_FILE_URI +courseId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(requestOk)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test UNAUTHORIZED");
		}
		

		//BAD_REQUEST
		try {

			MvcResult result =  mvc.perform(put(MODIFY_GROUP_FILE_URI +"not_A_Number")
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
		
		//NOT_MODIFIED
		fg.setId(4564564); //fake id
		String requestKO = FileTestUtils.fileGroup2Json(fg);
		try {

			MvcResult result =  mvc.perform(put(MODIFY_GROUP_FILE_URI +courseId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestKO)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.NOT_FOUND.value();

			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test NOT_MODIFIED");
		}
		
		
		
	}
	
	@Test
	void testEditFileOrder() {
		// api-files/edit/file-order/course/{courseId}/file/{fileId}/from/{sourceID}/to/{targetId}/pos/";//newPosition
		
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "this is the info", true);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
				
		FileGroup fg = new FileGroup("New FileGroup");
		fg = FileTestUtils.newFileGroup(mvc, httpSession, fg, c);
		fg = FileTestUtils.uploadTestFile(mvc, httpSession, fg, c);
		long firstFileId = fg.getFiles().get(0).getId();
		fg = FileTestUtils.uploadOtherTestFile(mvc, httpSession, fg, c);

		FileGroup fg2 = new FileGroup("Other FileGroup");
		fg2 = FileTestUtils.newFileGroup(mvc, httpSession, fg2, c);
		fg2 = FileTestUtils.uploadOtherTestFile(mvc, httpSession, fg2, c);
		
		try {

			MvcResult result =  mvc.perform(put(EDIT_ORDER_URI.replace("{courseId}", ""+c.getId())
															 .replace("{fileId}", ""+firstFileId)
															 .replace("{sourceID}", ""+fg.getId())
															 .replace("{targetId}", ""+fg2.getId())+ "0")
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();

			List<FileGroup> fglst = FileTestUtils.json2fileGroupList(result.getResponse().getContentAsString());
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
			/*check the filegroups*/
			assertEquals(1, fglst.get(0).getFiles().size(), "failure - not moved");
			assertEquals(2, fglst.get(1).getFiles().size(), "failure - not moved");
			
			assertEquals(0, fglst.get(0).getFiles().get(0).getIndexOrder(), "failure - order fail");
			assertEquals(0, fglst.get(1).getFiles().get(0).getIndexOrder(), "failure - order fail");
			assertEquals(1, fglst.get(1).getFiles().get(1).getIndexOrder(), "failure - order fail");

			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		
		//BAD_REQUEST
		try {

			MvcResult result =  mvc.perform(put(EDIT_ORDER_URI.replace("{courseId}", "not_a_long")
															 .replace("{fileId}", ""+firstFileId)
															 .replace("{sourceID}", ""+fg.getId())
															 .replace("{targetId}", ""+fg2.getId())+ "0")
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
		
		//UNAUTHORIZED
		try {

			MvcResult result =  mvc.perform(put(EDIT_ORDER_URI.replace("{courseId}", ""+c.getId())
															 .replace("{fileId}", ""+firstFileId)
															 .replace("{sourceID}", ""+fg.getId())
															 .replace("{targetId}", ""+fg2.getId())+ "0")
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();

			
			
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test BAD_REQUEST");
		}
	}
	

	@Test
	void testModifyFile() {
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "this is the info", true);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
				
		FileGroup fg = new FileGroup("New FileGroup");
		fg = FileTestUtils.newFileGroup(mvc, httpSession, fg, c);
		
		File fNotExisting = new File(1,"no Exists");
		String notModified = FileTestUtils.file2Json(fNotExisting);
		
		//NOT_MODIFIED 1
		try {

			MvcResult result =  mvc.perform(put(MODIFY_FILE_URI.replace("{fileGroupId}", ""+fg.getId())+c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(notModified)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.NOT_MODIFIED.value();
	
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test NOT_MODIFIED 1");
		}
		
		//NOT_MODIFIED 2
		try {

			MvcResult result =  mvc.perform(put(MODIFY_FILE_URI.replace("{fileGroupId}", "564")+c.getId())//notExisting fileGroup
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(notModified)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.NOT_MODIFIED.value();
	
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
		
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test NOT_MODIFIED 2");
		}
				
		//OK preparation
		fg = FileTestUtils.uploadTestFile(mvc, httpSession, fg, c);
		
		File f = fg.getFiles().get(0);
		f.setName("Modified File");
		
		String requestOK = FileTestUtils.file2Json(f);
		try {

			MvcResult result =  mvc.perform(put(MODIFY_FILE_URI.replace("{fileGroupId}", ""+fg.getId())+c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestOK)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();

			FileGroup fgR = FileTestUtils.json2FileGroup(result.getResponse().getContentAsString());
			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
			
			/*check the filename*/
			assertEquals("Modified File", fgR.getFiles().get(0).getName(), "failure - not modified");
		
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test OK");
		}
		//BAD_REQUEST
		try {

			MvcResult result =  mvc.perform(put(MODIFY_FILE_URI.replace("{fileGroupId}", ""+fg.getId())+"not_a_long")
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
		
		//UNAUTHORIZED
		try {

			MvcResult result =  mvc.perform(put(MODIFY_FILE_URI.replace("{fileGroupId}", ""+fg.getId())+c.getId())
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
	}
	
	
	@Test
	void testDeleteFileGroup() {
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "this is the info", true);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
				
		FileGroup fg = new FileGroup("New FileGroup");
		fg = FileTestUtils.newFileGroup(mvc, httpSession, fg, c);
		
		//OK
		try {

			MvcResult result =  mvc.perform(delete(DELETE_GROUP_URI.replace("{fileGroupId}", ""+fg.getId())+c.getId())
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
		
		//Bad Request
		try {

			MvcResult result =  mvc.perform(delete(DELETE_GROUP_URI.replace("{fileGroupId}", ""+fg.getId())+c.getId())
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
		
		//BAD_REQUEST
		try {

			MvcResult result =  mvc.perform(delete(DELETE_GROUP_URI.replace("{fileGroupId}", ""+fg.getId())+"not_a_long")
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

			MvcResult result =  mvc.perform(delete(DELETE_GROUP_URI.replace("{fileGroupId}", ""+fg.getId())+c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();

			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
					
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test UNAUTHORIZED");
		}
		
	}
	
	@Test
	void testDeleteFile() {
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "this is the info", true);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);
				
		FileGroup fg = new FileGroup("New FileGroup");
		fg = FileTestUtils.newFileGroup(mvc, httpSession, fg, c);
		
		fg = FileTestUtils.uploadTestFile(mvc, httpSession, fg, c);
		long firstFileId = fg.getFiles().get(0).getId();
		
		
		//OK
		try {

			MvcResult result =  mvc.perform(delete(DELETE_FILE_URI.replace("{fileGroupId}", ""+fg.getId())
																 .replace("{fileId}", ""+firstFileId)+c.getId())
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

		//BAD_REQUEST
		try {

			MvcResult result =  mvc.perform(delete(DELETE_FILE_URI.replace("{fileGroupId}", ""+fg.getId())
					 											 .replace("{fileId}", ""+firstFileId)+"not_a_long")
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

			MvcResult result =  mvc.perform(delete(DELETE_FILE_URI.replace("{fileGroupId}", ""+fg.getId())
					 											 .replace("{fileId}", ""+firstFileId)+c.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                ).andReturn();
	
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.UNAUTHORIZED.value();

			
			assertEquals(expected, status, "failure - expected HTTP status "+expected);
					
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //test UNAUTHORIZED");
		}
		
	}

}
