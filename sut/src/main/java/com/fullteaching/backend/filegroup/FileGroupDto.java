package com.fullteaching.backend.filegroup;

import com.fullteaching.backend.IdRef;

public class FileGroupDto {

    private long id;
    private String title;
    private IdRef fileGroupParent;

    public FileGroupDto() {
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

    public IdRef getFileGroupParent() {
        return fileGroupParent;
    }

    public void setFileGroupParent(IdRef fileGroupParent) {
        this.fileGroupParent = fileGroupParent;
    }

    /** Returns the parent's ID, or null if this is a root FileGroup. */
    public Long getFileGroupParentId() {
        return fileGroupParent != null ? fileGroupParent.getId() : null;
    }
}
