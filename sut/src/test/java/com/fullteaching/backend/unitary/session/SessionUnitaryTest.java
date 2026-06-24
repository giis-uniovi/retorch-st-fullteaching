package com.fullteaching.backend.unitary.session;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.utils.CourseTestUtils;
import com.fullteaching.backend.user.User;

class SessionUnitaryTest extends AbstractUnitTest {

	private static final String SESSION_TITLE = "Session Title";
	private static final String SESSION_DESCRIPTION = "Session Description";

	@Test
	void newSessionTest() {
		//Empty Session
		Session emptySession = new Session();
		assertNotNull(emptySession);
		
		//Not empty
		long date = System.currentTimeMillis();
		Session session = new Session(SESSION_TITLE, SESSION_DESCRIPTION, date);
		assertNotNull(session);
        assertEquals((long) date, session.getDate());
        assertEquals(SESSION_TITLE, session.getTitle());
        assertEquals(SESSION_DESCRIPTION, session.getDescription());
		
		//with course 
		String[] roles = {"STUDENT"};
		User u = new User("mock_teacher","mock2222","t_mocky", null,roles);
		Course c= CourseTestUtils.newCourseWithCd("course", u, null, "this is the info", false);
		
		Session cSession = new Session(SESSION_TITLE, SESSION_DESCRIPTION, date, c);
		assertNotNull(cSession);
        assertEquals((long) date, cSession.getDate());
        assertEquals(SESSION_TITLE, cSession.getTitle());
        assertEquals(SESSION_DESCRIPTION, cSession.getDescription());
        assertEquals(c, cSession.getCourse());
	}


	@Test
	void setAndGetSessionTitleTest() {
		Session session = new Session();
		session.setTitle(SESSION_TITLE);
		assertNotNull(session);
        assertEquals(SESSION_TITLE, session.getTitle());
	}

	@Test
	void setAndGetSessionDescriptionTest() {
		Session session = new Session();
		session.setDescription(SESSION_DESCRIPTION);
		assertNotNull(session);
        assertEquals(SESSION_DESCRIPTION, session.getDescription());
	}

	@Test
	void setAndGetSessionDateTest() {
		Session session = new Session();
		long date = System.currentTimeMillis();
		session.setDate(date);
		assertNotNull(session);
        assertEquals(date, session.getDate());
	}

	@Test
	void setAndGetSessionCourseTest() {
		String[] roles = {"STUDENT"};
		User u = new User("mock_teacher","mock2222","t_mocky", null,roles);
		Course c= CourseTestUtils.newCourseWithCd("course", u, null, "this is the info", false);
		
		Session session = new Session();
		session.setCourse(c);
		assertNotNull(session);
        assertEquals(c, session.getCourse());
	}

	@Test
	void equalSessionTest() {
		Session session1 = new Session();
		session1.setId(1);
		Session session2 = new Session();
		session2.setId(1);
		Session session3 = new Session();
		session3.setId(2);
		assertNotNull(session1);
		assertNotNull(session2);
		assertNotNull(session3);
        assertEquals(session1, session2);
        assertEquals(session1, session1);
        assertNotEquals(null, session1);
        assertNotEquals("not_a_session", session1);
        assertNotEquals(session1, session3);
		
	}

}
