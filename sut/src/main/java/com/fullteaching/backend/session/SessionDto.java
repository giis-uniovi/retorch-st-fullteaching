package com.fullteaching.backend.session;

public class SessionDto {

    private long id;
    private String title;
    private String description;
    private long date;

    public SessionDto() {
        // Required by Jackson for deserialization
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDate() {
        return date;
    }
}
