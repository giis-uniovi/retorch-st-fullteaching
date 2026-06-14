package com.fullteaching.backend.unitary.course;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.user.User;

import java.util.concurrent.ThreadLocalRandom;

class CourseUnitaryTest extends AbstractUnitTest {

	private static final String TITLE = "CURSO de Prueba";
	private static final String IMAGE = "Mock_image";
	private static User teacher;
	
	
	@BeforeAll
	static void initialize() {
		String [] roles = {"ROLE_TEACHER"};
		teacher = new User("mock_teacher","mock2222","t_mocky", null,roles);
	}

	@Test
	void newCourseTest() {
		Course c2 = new Course();
		assertNotNull(c2);
		
		Course c = new Course(TITLE, IMAGE, teacher);
		assertNotNull(c);
        assertEquals(c.getTeacher(), teacher);
        assertEquals(IMAGE, c.getImage());
        assertEquals(TITLE, c.getTitle());
		assertNotNull(c.getSessions());
		assertNotNull(c.getAttenders());
		assertNull(c.getCourseDetails());
		
		CourseDetails cd = new CourseDetails();
		
		Course c3 = new Course(TITLE, IMAGE, teacher, cd);
		assertNotNull(c3);
        assertEquals(c3.getTeacher(), teacher);
        assertEquals(IMAGE, c3.getImage());
        assertEquals(TITLE, c3.getTitle());
		assertNotNull(c3.getSessions());
		assertNotNull(c3.getAttenders());
		assertNotNull(c3.getCourseDetails());

        assertEquals(c3.getCourseDetails(), cd);
	}


	@Test
	void setAndGetCourseTitleTest() {
		Course c = new Course();
		c.setTitle(TITLE);
        assertEquals(TITLE, c.getTitle());
	}

	@Test
	void setAndGetCourseImageTest() {
		Course c = new Course();
		c.setImage(IMAGE);
        assertEquals(IMAGE, c.getImage());
	}

	@Test
	void setAndGetCourseTeacherTest() {
		Course c = new Course();
		c.setTeacher(teacher);
        assertEquals(c.getTeacher(), teacher);
	}

	@Test
	void setAndGetCourseDetailsTest() {
		Course c = new Course();
		c.setCourseDetails(new CourseDetails());
		assertNotNull(c.getCourseDetails());
	}

	@Test
	void equalCourseTest() {
		CourseDetails cd = new CourseDetails();

		Course c1 = new Course(TITLE, IMAGE, teacher, cd);
		c1.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
		Course c2 = new Course(TITLE, IMAGE, teacher);
		c2.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));

        assertEquals(c1, c1);
        assertNotEquals("not_a_course", c1);
        assertNotEquals(c1, c2);
        assertNotEquals(null, c1);
		
	}

}
