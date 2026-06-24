package com.fullteaching.backend.user;

import com.fullteaching.backend.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);

    Collection<User> findByNameIn(Collection<String> names);

    @Query("SELECT DISTINCT u FROM User u JOIN u.courses c WHERE c IN :courses")
    Collection<User> findByCourses(@Param("courses") Collection<Course> courses);

}
