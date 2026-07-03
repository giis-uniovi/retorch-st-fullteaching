package com.fullteaching.backend.comment;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractCommentContent {

    protected String message;
    protected String videourl;
    protected boolean audioonly;

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
}
