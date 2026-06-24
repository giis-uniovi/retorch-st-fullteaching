package com.fullteaching.backend.coursedetails;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.forum.Forum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseDetailsRepository extends JpaRepository<CourseDetails, Long> {

    CourseDetails findByCourse(Course course);

    CourseDetails findByForum(Forum forum);

}
