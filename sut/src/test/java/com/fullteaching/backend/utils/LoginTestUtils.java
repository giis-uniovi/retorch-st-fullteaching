package com.fullteaching.backend.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullteaching.backend.user.User;

public class LoginTestUtils {

	private static final String LOGIN_URI = "/api-logIn";
	private static final String NEW_USER_URI = "/api-users/new";

	public static HttpSession logIn(MockMvc mvc, String user, String password, User loggedUser) throws Exception {
		
		String userPass = user+":"+password;

		MvcResult resultLogin = mvc.perform(get(LOGIN_URI)
						.header("Authorization", "Basic "+utf8_to_b64(userPass))
						.header("X-Requested-With", "XMLHttpRequest")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
				).andReturn();
		
		System.out.println(resultLogin.getResponse());
		int statusLogin = resultLogin.getResponse().getStatus();

        assertEquals(statusLogin, HttpStatus.OK.value(), "failure login - expected HTTP status " +
                HttpStatus.OK.value() +
                " but was: " + statusLogin);
		if (loggedUser==null) {
			String content = resultLogin.getResponse().getContentAsString();
			loggedUser = json2User(content);
			resultLogin.getRequest().getSession().setAttribute("loggedUser", loggedUser);
		}
		return resultLogin.getRequest().getSession();
	}
	public static HttpSession logIn(MockMvc mvc, String user, String password) throws Exception {
		return logIn(mvc,user,password, null);
	}
	public static User registerUserIfNotExists(MockMvc mvc, String userParameters) throws Exception {
		
		MvcResult resultInsert = mvc.perform(post(NEW_USER_URI)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(userParameters)
				).andReturn();	
		
		int statusInsert = resultInsert.getResponse().getStatus();
		
		
		if (statusInsert == HttpStatus.CREATED.value()) {
			String content = resultInsert.getResponse().getContentAsString();
			return json2User(content);
		}
		
		return null;
	}
	
	
	public static String utf8_to_b64(String str)  {
		Base64.Encoder enc= Base64.getEncoder();
        byte[] strenc =enc.encode(str.getBytes(StandardCharsets.UTF_8));
        
        return new String(strenc);

	}
	
	public static User json2User(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, User.class);
	}
	
}
