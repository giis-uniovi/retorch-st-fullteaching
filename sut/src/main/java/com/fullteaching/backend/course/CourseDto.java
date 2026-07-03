package com.fullteaching.backend.course;

import com.fullteaching.backend.IdRef;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseDto extends AbstractCourseData {

    private CourseDetailsRef courseDetails;
    private Set<IdRef> attenders;

    public CourseDto() {
        // Required by Jackson for deserialization
    }

    public CourseDetailsRef getCourseDetails() {
        return courseDetails;
    }

    public void setCourseDetails(CourseDetailsRef courseDetails) {
        this.courseDetails = courseDetails;
    }

    public Set<IdRef> getAttenders() {
        return attenders;
    }

    public void setAttenders(Set<IdRef> attenders) {
        this.attenders = attenders;
    }

    /** Returns the IDs of all attenders, or an empty set if none were sent. */
    public Set<Long> getAttenderIds() {
        if (attenders == null) return Collections.emptySet();
        return attenders.stream().map(IdRef::getId).collect(Collectors.toCollection(HashSet::new));
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
}
