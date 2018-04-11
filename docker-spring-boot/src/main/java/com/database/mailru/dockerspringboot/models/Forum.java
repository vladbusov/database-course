package com.database.mailru.dockerspringboot.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;


@Entity
@Table(name = "forum")
public class Forum {

    @Id
    private Long id;
    private String slug;
    private String title;
    private String user;
    private Integer posts;
    private Integer threads;

    public Forum() {
        this.id = null;
        this.slug = null;
        this.title = null;
        this.user = null;
        this.posts = 0;
        this.threads = 0;
    }

    public Forum(Long id, String slug, String title, String user, Integer posts, Integer threads) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.user = user;
        this.threads = threads;
        this.posts = posts;
    }

    public Forum(Long id, String slug, String title, String user) {
        this.id = id;
        this.posts = 0;
        this.threads = 0;
        this.slug = slug;
        this.title = title;
        this.user = user;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forum forum = (Forum) o;
        return Objects.equals(id, forum.id) &&
                Objects.equals(slug, forum.slug) &&
                Objects.equals(title, forum.title) &&
                Objects.equals(user, forum.user) &&
                Objects.equals(posts, forum.posts) &&
                Objects.equals(threads, forum.threads);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, slug, title, user, posts, threads);
    }
}
