package com.database.mailru.dockerspringboot.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Vote {
    @Id
    private Long id;
    private String nickname;
    private Integer voice;
    private Long threadId;

    public Vote(Long id, String nickname, Integer voice, Long threadId) {
        this.id = id;
        this.nickname = nickname;
        this.voice = voice;
        this.threadId = threadId;
    }


    public Vote() {
        this.id = null;
        this.nickname = null;
        this.voice = 0;
        this.threadId = null;
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

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id) &&
                Objects.equals(nickname, vote.nickname) &&
                Objects.equals(voice, vote.voice) &&
                Objects.equals(threadId, vote.threadId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, nickname, voice, threadId);
    }
}
