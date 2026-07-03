package com.fullteaching.backend.course;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractCourseData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;
    protected String title;
    protected String image;

    @JsonView(SimpleCourseList.class)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonView(SimpleCourseList.class)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonView(SimpleCourseList.class)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public interface SimpleCourseList {
    }
}
