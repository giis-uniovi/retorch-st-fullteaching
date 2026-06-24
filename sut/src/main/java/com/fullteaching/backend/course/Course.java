package com.fullteaching.backend.course;

import com.fasterxml.jackson.annotation.JsonView;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Course extends AbstractCourseData {

    @ManyToOne
    private User teacher;
    @OneToOne(cascade = CascadeType.ALL)
    private CourseDetails courseDetails;
    @JsonView(SimpleCourseList.class)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "course")
    private Set<Session> sessions;
    @ManyToMany
    private Set<User> attenders;

    public Course() {
    }

    public Course(String title, String image, User teacher) {
        this(title, image, teacher, null);
    }

    public Course(String title, String image, User teacher, CourseDetails courseDetails) {
        this.title = title;
        this.image = image;
        this.teacher = teacher;
        this.courseDetails = courseDetails;
        this.sessions = new HashSet<>();
        this.attenders = new HashSet<>();
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public CourseDetails getCourseDetails() {
        return courseDetails;
    }

    public void setCourseDetails(CourseDetails courseDetails) {
        this.courseDetails = courseDetails;
    }

    public Set<User> getAttenders() {
        return attenders;
    }

    public void setAttenders(Set<User> attenders) {
        this.attenders = attenders;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Session> sessions) {
        this.sessions = sessions;
    }

    /**
     * Adds an attender to this course and maintains the bidirectional relationship.
     * Returns true if the attender was not already present on both sides.
     */
    public boolean addAttender(User attender) {
        boolean addedToCourse = this.attenders.add(attender);
        boolean addedToUser = attender.getCourses().add(this);
        return addedToCourse && addedToUser;
    }

    //To make 'user.getCourse().remove(course)' possible
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Course otherCourse)) return false;
        return (otherCourse.id == this.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Course[title: \"" + this.title + "\", teacher: \"" + this.teacher.getNickName() + "\", #attenders: " + this.attenders.size() + ", #sessions: " + this.sessions.size() + "]";
    }
}
