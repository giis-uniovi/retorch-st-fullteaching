package com.fullteaching.backend.comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fullteaching.backend.user.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Comment extends AbstractCommentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long date;
    @OneToMany(mappedBy = "commentParent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Comment> replies;
    @ManyToOne
    @JsonBackReference
    private Comment commentParent;
    @ManyToOne
    private User user;

    public Comment() {
    }

    public Comment(String message, long date, User user) {
        this(message, date, user, null);
    }

    public Comment(String message, long date, User user, Comment commentParent) {
        this.message = message;
        this.date = date;
        this.user = user;
        this.replies = new ArrayList<>();
        this.commentParent = commentParent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getCommentParent() {
        return commentParent;
    }

    public void setCommentParent(Comment commentParent) {
        this.commentParent = commentParent;
    }

    @Override
    public String toString() {
        String parent = this.commentParent != null ? commentParent.message : "null";
        String userName = this.user != null ? this.user.getNickName() : "";
        int nReplies = this.replies != null ? this.replies.size() : 0;
        return "Comment[message: \"" + this.message + "\", author: \"" + userName + "\", parent: \"" + parent + "\", #replies: " + nReplies + "date: \"" + this.date + "\"]";
    }

    public interface CommentNoParent {
    }

}
