package com.fullteaching.backend.unitary.coursedetails;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.filegroup.FileGroup;
import com.fullteaching.backend.forum.Forum;
import com.fullteaching.backend.user.User;

class CourseDetailsUnitaryTests extends AbstractUnitTest {

    @Test
    void newCourseDetailsTest() {
        String[] roles = {"TEACHER"};
        User u = new User("mock", "Pass1234", "mock", null, roles);

        CourseDetails cd = new CourseDetails();
        assertNotNull(cd);

        Course c = new Course("to modify", "/../assets/images/default_session_image.png", u);

        CourseDetails cd2 = new CourseDetails(c);

        assertNotNull(cd2);
        assertEquals(cd2.getCourse(), c);

    }

    @Test
    void setAndGetCourseDetailsInfoTest() {
        CourseDetails cd = new CourseDetails();
        cd.setInfo("this is info");
        assertNotNull(cd);
        assertEquals("this is info", cd.getInfo());
    }

    @Test
    void setAndGetCourseDetailsForumTest() {
        CourseDetails cd = new CourseDetails();
        Forum forum = new Forum();
        cd.setForum(forum);
        assertNotNull(cd);
        assertEquals(forum, cd.getForum());
    }

    @Test
	void setAndGetCourseDetailsFilesTest() {
        CourseDetails cd = new CourseDetails();
        List<FileGroup> files = new ArrayList<FileGroup>();
        cd.setFiles(files);
        assertNotNull(cd);
        assertEquals(files, cd.getFiles());
    }

    @Test
    void SetAndGetCourseDetailsCourseTest() {
        CourseDetails cd = new CourseDetails();
        String[] roles = {"TEACHER"};
        User u = new User("mock", "Pass1234", "mock", null, roles);

        Course c = new Course("to modify", "/../assets/images/default_session_image.png", u);

        cd.setCourse(c);
        assertNotNull(cd);
        assertEquals(cd.getCourse(), c);

    }

}
