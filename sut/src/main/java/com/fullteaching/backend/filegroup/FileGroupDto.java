package com.fullteaching.backend.filegroup;

public class FileGroupDto {

    private long id;
    private String title;
    private FileGroupParentRef fileGroupParent;

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

    public FileGroupParentRef getFileGroupParent() {
        return fileGroupParent;
    }

    public void setFileGroupParent(FileGroupParentRef fileGroupParent) {
        this.fileGroupParent = fileGroupParent;
    }

    /** Returns the parent's ID, or null if this is a root FileGroup. */
    public Long getFileGroupParentId() {
        return fileGroupParent != null ? fileGroupParent.getId() : null;
    }

    /** Captures only the id from the nested fileGroupParent object in the JSON. */
    public static class FileGroupParentRef {

        private long id;

        public FileGroupParentRef() {
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
