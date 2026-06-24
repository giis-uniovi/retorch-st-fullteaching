package com.fullteaching.backend.comment;

import com.fullteaching.backend.IdRef;

public class CommentDto extends AbstractCommentContent {

    private IdRef commentParent;

    public CommentDto() {
        // Required by Jackson for deserialization
    }

    public IdRef getCommentParent() {
        return commentParent;
    }

    public void setCommentParent(IdRef commentParent) {
        this.commentParent = commentParent;
    }
}
