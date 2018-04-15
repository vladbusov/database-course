package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.ThreadModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ThreadMapper {

    public static final RowMapper<ThreadModel> THREAD_MAPPER = (res, num) -> {
        final Timestamp timestamp = res.getTimestamp("created");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Long id = res.getLong("id");
        String title = res.getString("title");
        String slug = res.getString("slug");
        String author = res.getString("author");
        String message = res.getString("message");
        String forum = res.getString("forum");
        Integer votes = res.getInt("votes");

        return new ThreadModel(id,slug,author,forum,dateFormat.format(timestamp.getTime()),message,title,votes);
    };

}
