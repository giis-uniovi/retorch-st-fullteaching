/**
 * 
 */
package com.fullteaching.backend.unitary.user;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.user.User;

/**
 * @author gtunon
 *
 */

class UserUnitaryTest extends AbstractUnitTest {

	/*Test user data*/
	String name = "TestUser";
	String password = "blablaba";
	String nickName = "testi";
	String picture = "picture/test.jpg";
	String[] roles = {"STUDENT"};
	
	
	/**
	 * Test method for {@link com.fullteaching.backend.user.User#User(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])}
	 * and {@link com.fullteaching.backend.user.User#User()}.
	 */
	@Test
	void newUserTest() {
		
		//Empty user
		User emptyUser = new User();
		assertNotNull(emptyUser, "User failed to be created");
		
		//User with picture
		User u = new User(name, password, nickName, picture,roles);
		assertNotNull(u, "User failed to be created");
        assertEquals(name, u.getName(), "User failed to be created");
		assertTrue((new BCryptPasswordEncoder()).matches(password, u.getPasswordHash()), "User failed to be created");
        assertEquals(nickName, u.getNickName(), "User failed to be created");
        assertEquals(picture, u.getPicture(), "User failed to be created");
        assertEquals(roles.length, u.getRoles().size(), "User failed to be created");
		
		//user witout picture
		u = new User(name, password, nickName, null,roles);
		assertNotNull(u, "User failed to be created");
        assertEquals(name, u.getName(), "User failed to be created");
		assertTrue((new BCryptPasswordEncoder()).matches(password, u.getPasswordHash()), "User failed to be created");
        assertEquals(nickName, u.getNickName(), "User failed to be created");
		assertNotNull(u.getPicture(), "User failed to be created");
        assertEquals(roles.length, u.getRoles().size(), "User failed to be created");
	}

	/**
	 * Test method for {@link com.fullteaching.backend.user.User#getName()}.
	 * and {@link com.fullteaching.backend.user.User#setName(java.lang.String)}.
	 */
	@Test
	void setAndGetUserNameTest() {
		User u = new User();
		u.setName(name);
        assertEquals(name, u.getName(), "testSetAndGetUserName FAIL");
	}


	/**
	 * Test method for {@link com.fullteaching.backend.user.User#setPasswordHash(java.lang.String)}
	 * and {@link com.fullteaching.backend.user.User#getPasswordHash()}.
	 */
	@Test
	void setAndGetHashPasswordTest() {
		User u = new User();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		u.setPasswordHash(encoder.encode(password));
		assertTrue(encoder.matches(password, u.getPasswordHash()), "setAndGetHashPasswordTest FAIL");
	}
	
	/**
	 * Test method for {@link com.fullteaching.backend.user.User#getRoles()}
	 * and  {@link com.fullteaching.backend.user.User#setRoles(java.util.List)}.
	 */
	@Test
	void setAndGetUserRolesTest() {
		User u = new User();	
		u.setRoles(Arrays.asList(roles));
        assertEquals(roles.length, u.getRoles().size(), "SetAndGetUserRolesTest FAIL");
	}

	/**
	 * Test method for {@link com.fullteaching.backend.user.User#getNickName()} 
	 * and {@link com.fullteaching.backend.user.User#setNickName(java.lang.String)}.
	 */
	@Test
	void setAndGetUserNickNameTest() {
		User u = new User();
		u.setNickName(nickName);
        assertEquals(nickName, u.getNickName(), "SetAndGetUserNickNameTest FAIL");
	}

	/**
	 * Test method for {@link com.fullteaching.backend.user.User#getPicture()} 
	 * and {@link com.fullteaching.backend.user.User#setPicture(java.lang.String)}.
	 */
	@Test
	void setAndGetUserPictureTest() {
		User u = new User();
		u.setPicture(picture);
        assertEquals(picture, u.getPicture(), "SetAndGetUserPictureTest FAIL");
	}

	/**
	 * Test method for {@link com.fullteaching.backend.user.User#getRegistrationDate()} 
	 * and {@link com.fullteaching.backend.user.User#setRegistrationDate(long)}.
	 */
	@Test
	void setAndGetUserRegistrationDateTest() {
		User u = new User();
		long date = System.currentTimeMillis();
		u.setRegistrationDate(date);
        assertEquals((long) date, u.getRegistrationDate());
	}


	/**
	 * Test method for {@link com.fullteaching.backend.user.User#equals(java.lang.Object)}.
	 */
	@Test
	void equalUserTest() {
		User u1 = new User(name, password, nickName, picture,roles);
		User u2 = new User(name, password, nickName, picture,roles);
        assertEquals(u1, u2, "EqualUserTest FAIL");
        assertNotEquals("not An User", u1, "EqualUserTest FAIL");
        assertEquals(u1, u1, "EqualUserTest FAIL");
        assertNotEquals(null, u1, "EqualUserTest FAIL");
	}



}
