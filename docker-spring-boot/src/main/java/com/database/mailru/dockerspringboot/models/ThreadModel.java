package com.database.mailru.dockerspringboot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "thread")
public class ThreadModel {
    @Id
    private Long id;
    private String slug;
    private String author;
    private String forum;
    private String created;
    private String message;
    private String title;
    @JsonIgnore
    private Integer votes;

    public ThreadModel(Long id, String slug, String author, String forum, String created, String message, String title, Integer votes) {
        this.id = id;
        this.slug = slug;
        this.author = author;
        this.forum = forum;
        this.created = created;
        this.message = message;
        this.title = title;
        this.votes = votes;
    }

    public ThreadModel(Long id, String slug, String author, String forum, String created, String message, String title) {
        this.id = id;
        this.slug = slug;
        this.author = author;
        this.forum = forum;
        this.created = created;
        this.message = message;
        this.title = title;
        this.votes = 0;
    }


    public ThreadModel() {
        this.id = null;
        this.slug = null;
        this.author = null;
        this.forum = null;
        this.created = null;
        this.message = null;
        this.title = null;
        this.votes = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadModel threadModel = (ThreadModel) o;
        return Objects.equals(id, threadModel.id) &&
                Objects.equals(slug, threadModel.slug) &&
                Objects.equals(author, threadModel.author) &&
                Objects.equals(forum, threadModel.forum) &&
                Objects.equals(created, threadModel.created) &&
                Objects.equals(message, threadModel.message) &&
                Objects.equals(title, threadModel.title) &&
                Objects.equals(votes, threadModel.votes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, slug, author, forum, created, message, title, votes);
    }
}
