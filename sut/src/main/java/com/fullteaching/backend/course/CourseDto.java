package com.fullteaching.backend.course;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseDto {

    private long id;
    private String title;
    private String image;
    private CourseDetailsRef courseDetails;
    private Set<UserRef> attenders;

    public CourseDto() {
        // Required by Jackson for deserialization
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CourseDetailsRef getCourseDetails() {
        return courseDetails;
    }

    public void setCourseDetails(CourseDetailsRef courseDetails) {
        this.courseDetails = courseDetails;
    }

    public Set<UserRef> getAttenders() {
        return attenders;
    }

    public void setAttenders(Set<UserRef> attenders) {
        this.attenders = attenders;
    }

    /** Returns the IDs of all attenders, or an empty set if none were sent. */
    public Set<Long> getAttenderIds() {
        if (attenders == null) return Collections.emptySet();
        return attenders.stream().map(UserRef::getId).collect(Collectors.toCollection(HashSet::new));
    }

    /** Captures id, info, and forum.activated from the nested courseDetails object in the JSON. */
    public static class CourseDetailsRef {

        private String info;
        private ForumRef forum;

        public CourseDetailsRef() {
            // Required by Jackson for deserialization
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public ForumRef getForum() {
            return forum;
        }

        public void setForum(ForumRef forum) {
            this.forum = forum;
        }

        public static class ForumRef {

            private boolean activated;

            public ForumRef() {
                // Required by Jackson for deserialization
            }

            public boolean isActivated() {
                return activated;
            }

            public void setActivated(boolean activated) {
                this.activated = activated;
            }
        }
    }

    /** Captures only the id from each attender object in the JSON. */
    public static class UserRef {

        private long id;

        public UserRef() {
            // Required by Jackson for deserialization
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}
