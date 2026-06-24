package com.fullteaching.backend.entry;

import java.util.List;

public class EntryDto {

    private String title;
    private List<CommentRef> comments;

    public EntryDto() {
        // Required by Jackson for deserialization
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CommentRef> getComments() {
        return comments;
    }

    public void setComments(List<CommentRef> comments) {
        this.comments = comments;
    }

    /** Captures only the message from each comment object in the JSON. */
    public static class CommentRef {

        private String message;

        public CommentRef() {
            // Required by Jackson for deserialization
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
