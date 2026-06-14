package com.fullteaching.backend.course;

import com.fullteaching.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c FROM Course c JOIN c.attenders u WHERE u IN :users")
    Collection<Course> findByAttenders(@Param("users") Collection<User> users);

}
