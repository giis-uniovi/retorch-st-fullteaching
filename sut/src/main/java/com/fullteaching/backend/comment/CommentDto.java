package com.fullteaching.backend.comment;

public class CommentDto {

    private String message;
    private String videourl;
    private boolean audioonly;
    private CommentParentRef commentParent;

    public CommentDto() {
        // Required by Jackson for deserialization
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public boolean getAudioonly() {
        return audioonly;
    }

    public void setAudioonly(boolean audioonly) {
        this.audioonly = audioonly;
    }

    public CommentParentRef getCommentParent() {
        return commentParent;
    }

    public void setCommentParent(CommentParentRef commentParent) {
        this.commentParent = commentParent;
    }

    /** Captures only the id from the nested commentParent object in the JSON. */
    public static class CommentParentRef {

        private long id;

        public CommentParentRef() {
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
