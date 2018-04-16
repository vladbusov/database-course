package com.database.mailru.dockerspringboot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty(value = "isEdited")
    private Boolean isEdited;

    private String created;
    private String path;

    public Post(Long id, String author, String forum, String message, Long parent, Long thread, Boolean isEdited, String created, String path) {
        this.id = id;
        this.author = author;
        this.forum = forum;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.isEdited = isEdited;
        this.created = created;
        this.path = path;
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
        this.path = null;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
                Objects.equals(created, post.created) &&
                Objects.equals(path, post.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, author, forum, message, parent, thread, isEdited, created, path);
    }
}
