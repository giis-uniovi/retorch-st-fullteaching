package com.fullteaching.backend.entry;

import com.fullteaching.backend.comment.Comment;

public class NewEntryCommentResponse {

    private Entry entry;
    private Comment comment;

    public NewEntryCommentResponse(Entry entry, Comment comment) {
        this.entry = entry;
        this.comment = comment;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

}