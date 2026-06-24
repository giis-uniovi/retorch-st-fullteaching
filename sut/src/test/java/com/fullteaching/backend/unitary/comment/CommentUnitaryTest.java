package com.fullteaching.backend.unitary.comment;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.user.User;

class CommentUnitaryTest extends AbstractUnitTest {

	@Test
	void newForumEntryCommentTest() {
		Comment cm = new Comment();
		assertNotNull(cm);
		
		String[] roles = {"TEACHER"};
		User u =  new User("mock", "Pass1234", "mock", null, roles);
		Long date = System.currentTimeMillis();
		String message = "This is the message";
		Comment cm2 = new Comment(message, date, u);
	
		assertNotNull(cm2);
		assertNotNull(cm2.getReplies());
        assertEquals(u, cm2.getUser());
        assertEquals((long) date, cm2.getDate());
        assertEquals(message, cm2.getMessage());
		
		Comment cm3 = new Comment(message, date, u, cm2);
		
		assertNotNull(cm3);
		assertNotNull(cm3.getReplies());
        assertEquals(u, cm3.getUser());
        assertEquals((long) date, cm3.getDate());
        assertEquals(message, cm3.getMessage());
        assertEquals(cm2, cm3.getCommentParent());
	}


	@Test
	void setAndGetCommentMessageTest() {
		Comment cm = new Comment();
		String message = "This is the message";
		cm.setMessage(message);
		assertNotNull(cm);
        assertEquals(message, cm.getMessage());
	}

	@Test
	void setAndGetCommentDateTest() {
		Comment cm = new Comment();
		long date = System.currentTimeMillis();
		cm.setDate(date);
		assertNotNull(cm);
        assertEquals(date, cm.getDate());
	}

	@Test
	void setAndGetCommentRepliesTest() {
		String[] roles = {"TEACHER"};
		User u =  new User("mock", "Pass1234", "mock", null, roles);
		long date = System.currentTimeMillis();
		String message = "This is the message";
		Comment rep = new Comment(message, date, u);
		
		List<Comment> replies = new ArrayList<Comment>();
		replies.add(rep);
		
		Comment cm = new Comment();
		cm.setReplies(replies);
		assertNotNull(cm);
		assertNotNull(cm.getReplies());
        assertEquals(replies, cm.getReplies());
	}

	@Test
	void setAndGetCommentUserTest() {
		String[] roles = {"TEACHER"};
		User u =  new User("mock", "Pass1234", "mock", null, roles);
		
		Comment cm = new Comment();
		cm.setUser(u);
		assertNotNull(cm);
	}

	@Test
	void setAndGetCommentParentTest() {
		String[] roles = {"TEACHER"};
		User u =  new User("mock", "Pass1234", "mock", null, roles);
		long date = System.currentTimeMillis();
		String message = "This is the message";
		Comment parent = new Comment(message, date, u);
		
		Comment cm = new Comment();
		cm.setCommentParent(parent);
		assertNotNull(cm);
	}

}
