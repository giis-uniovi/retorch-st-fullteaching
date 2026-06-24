package com.fullteaching.backend.unitary.entry;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.user.User;

class EntryUnitaryTest extends AbstractUnitTest {

	@BeforeEach
	void setUp() throws Exception {
		// Added by convention, not implemented
	}


	@Test
	void newForumEntryTest() {
		String[] roles = {"TEACHER"};
		User u =  new User("mock", "Pass1234", "mock", null, roles);
		long date = System.currentTimeMillis();
		
		Entry e2 = new Entry();
		assertNotNull(e2);
		
		Entry e = new Entry("Test Entry",date,u);
		assertNotNull(e);
        assertEquals("Test Entry", e.getTitle());
        assertEquals(date, e.getDate());
        assertEquals(u, e.getUser());
	}

	@Test
	void setAndGetEntryTitleTest() {
		Entry e = new Entry();
		e.setTitle("This title");
		assertNotNull(e);
        assertEquals("This title", e.getTitle());
	}

	@Test
	void setAndGetEntryDateTest() {
		Entry e = new Entry();
		long date = System.currentTimeMillis();
		e.setDate(date);
		assertNotNull(e);
        assertEquals(date, e.getDate());

	}

	@Test
	void setAndGetEntryUserTest() {
		String[] roles = {"TEACHER"};

		User u =  new User("mock", "Pass1234", "mock", null, roles);

		Entry e = new Entry();
		assertNotNull(e);
		e.setUser(u);
        assertEquals(u, e.getUser());

	}

	@Test
	void setAndGetEntryCommentsTest() {

		List<Comment> comments = new ArrayList<Comment>();
		
		Entry e = new Entry();
		e.setComments(comments);
		assertNotNull(e);
        assertEquals(comments, e.getComments());

	}

}
