package com.fullteaching.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.file.File;
import com.fullteaching.backend.filegroup.FileGroup;
import com.google.gson.Gson;

public class FileTestUtils {

	private static final Logger log = LoggerFactory.getLogger(FileTestUtils.class);
	
	private static final String NEW_FILE_URI ="/api-files/";
	private static final String UPLOAD_URI ="/api-load-files/upload/course/{courseId}/file-group/";
	
	private static final MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
	private static final MockMultipartFile secondFile = new MockMultipartFile("data", "other.txt", "text/plain", "some other xml".getBytes());

	public static FileGroup newFileGroup(MockMvc mvc, HttpSession httpSession, FileGroup fg, Course c) {
		
		long courseId = c.getCourseDetails().getId();
		String requestOk = fileGroup2Json(fg);
		
		try {

			MvcResult result =  mvc.perform(post(NEW_FILE_URI +courseId)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                .content(requestOk)
					                ).andReturn();		

			String content = result.getResponse().getContentAsString();
			CourseDetails cd = CourseTestUtils.json2CourseDetails(content);
			
			return getFileGroupFromCd(cd, fg.getTitle());
		
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //FileTestUtils.newFileGroup ::"+e.getClass().getName());
		}
		
		return null;
	}
	
	public static FileGroup uploadTestFile(MockMvc mvc, HttpSession httpSession, FileGroup fg, Course c, MockMultipartFile file) {
		
		try {
			MvcResult result =  mvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_URI.replace("{courseId}",""+c.getId())+fg.getId())
	                .file(file)
	                .session((MockHttpSession) httpSession)
	                ).andReturn();
	
			String content = result.getResponse().getContentAsString();
			System.out.println(content);
			return json2FileGroup(content);
			
		} catch (Exception e) {
			log.error("Unexpected exception during test execution", e);
			fail("EXCEPTION: //FileTestUtils.uploadTestFile ::"+e.getClass().getName());
		}
		return null;
	}

	public static FileGroup uploadTestFile(MockMvc mvc, HttpSession httpSession, FileGroup fg, Course c) {
		return uploadTestFile(mvc,httpSession,fg,c,firstFile);
	}
	
	public static FileGroup uploadOtherTestFile(MockMvc mvc, HttpSession httpSession, FileGroup fg, Course c) {
		return uploadTestFile(mvc,httpSession,fg,c,secondFile);
	}
	
	public static FileGroup json2FileGroup(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		json = json.replaceAll("\"" + "fileExtension" + "\"[ ]*:[^,}\\]]*[,]?", "");
		json = json.replaceAll(",}","}");
		return mapper.readValue(json, FileGroup.class);
	}
	
	
	public static String fileGroup2Json(FileGroup fg) {
		if(fg!=null) {
			Gson gson = new Gson();
			return gson.toJson(fg);
		}
		else {
			throw new NullPointerException("FileGroup is Null");
		}
	}
	
	public static String file2Json(File f) {
		if(f!=null) {
			Gson gson = new Gson();
			return gson.toJson(f);
		}
		else {
			throw new NullPointerException("file is Null");
		}
	}
	
	public static FileGroup getFileGroupFromCd(CourseDetails cd, String name) {
		List<FileGroup> list = cd.getFiles();
		return getFileGroupByName(list,name);
		
	}
	
	public static List<FileGroup> json2fileGroupList(String json) throws IOException, org.json.JSONException {
		json = json.replaceAll("\"" + "fileExtension" + "\"[ ]*:[^,}\\]]*[,]?", "");
		json = json.replaceAll(",}","}");
		
		List<FileGroup> fglst = new ArrayList<FileGroup>();
		
		JSONArray jsonarray = new JSONArray(json);
		
		for (int i = 0; i < jsonarray.length(); i++) {
		    JSONObject jsonobject = jsonarray.getJSONObject(i);
		    fglst.add(json2FileGroup(jsonobject.toString()));
		}
		
		return fglst;
		
	}
	
	private static FileGroup getFileGroupByName(List<FileGroup> list, String name) {
		
		for (FileGroup ele : list) {
			if (name.equals(ele.getTitle()))
				return ele;
			if (ele.getFileGroups()!=null && ele.getFileGroups().size()>0)
				return getFileGroupByName(ele.getFileGroups(), name);
		}
		
		return null;
	}
	
	
}
