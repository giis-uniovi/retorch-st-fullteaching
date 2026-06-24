package com.fullteaching.backend.file;

public class FileDto {

    private long id;
    private String name;

    public FileDto() {
        // Required by Jackson for deserialization
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
