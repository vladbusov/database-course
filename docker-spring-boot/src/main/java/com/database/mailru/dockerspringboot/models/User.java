package com.database.mailru.dockerspringboot.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;
    private String nickname;
    private String fullname;
    private String email;
    private String about;

    public User() {
        this.id = null;
        this.nickname = null;
        this.fullname = null;
        this.email = null;
        this.about = null;
    }


    public User(Long id, String nickname, String fullname, String email, String about) {
        this.id = id;
        this.nickname = nickname;
        this.fullname = fullname;
        this.email = email;
        this.about = about;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(nickname, user.nickname) &&
                Objects.equals(fullname, user.fullname) &&
                Objects.equals(email, user.email) &&
                Objects.equals(about, user.about);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, nickname, fullname, email, about);
    }
}
