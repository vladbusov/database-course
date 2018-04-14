package com.database.mailru.dockerspringboot.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Post {
    @Id
    private Long id;
    private String author;
    private String forum;
    private String message;
    private Long parent;
    private Long thread;
    private Boolean isEdited;
    private Timestamp created;

    public Post(Long id, String author, String forum, String message, Long parent, Long thread, Boolean isEdited, Timestamp created) {
        this.id = id;
        this.author = author;
        this.forum = forum;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.isEdited = isEdited;
        this.created = created;
    }


    public Post() {
        this.id = null;
        this.author = null;
        this.forum = null;
        this.message = null;
        this.parent = 0L;
        this.thread = null;
        this.isEdited = false;
        this.created = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getThread() {
        return thread;
    }

    public void setThread(Long thread) {
        this.thread = thread;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(author, post.author) &&
                Objects.equals(forum, post.forum) &&
                Objects.equals(message, post.message) &&
                Objects.equals(parent, post.parent) &&
                Objects.equals(thread, post.thread) &&
                Objects.equals(isEdited, post.isEdited) &&
                Objects.equals(created, post.created);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, author, forum, message, parent, thread, isEdited, created);
    }
}
