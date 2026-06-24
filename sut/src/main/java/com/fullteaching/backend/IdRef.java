package com.fullteaching.backend;

/** Captures only the {@code id} field from a nested JSON object (e.g. parent references in DTOs). */
public class IdRef {

    private long id;

    public IdRef() {
        // Required by Jackson for deserialization
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
