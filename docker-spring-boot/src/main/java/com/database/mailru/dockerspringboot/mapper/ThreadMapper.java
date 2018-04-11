package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.ThreadModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
public class ThreadMapper {

    public static final RowMapper<ThreadModel> THREAD_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        String title = res.getString("title");
        String slug = res.getString("slug");
        String author = res.getString("author");
        String message = res.getString("message");
        String forum = res.getString("forum");
        Timestamp created = res.getTimestamp("created");
        Integer votes = res.getInt("votes");

        return new ThreadModel(id,slug,author,forum,created,message,title,votes);
    };

}
