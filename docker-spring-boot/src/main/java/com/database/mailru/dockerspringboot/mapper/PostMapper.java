package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.Forum;
import com.database.mailru.dockerspringboot.models.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class PostMapper {
    public static final RowMapper<Post> POST_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        String author = res.getString("author");
        String forum = res.getString("forum");
        String message = res.getString("message");
        Long parent = res.getLong("parent");
        Long thread = res.getLong("thread");
        Boolean isEdited = res.getBoolean("isEdited");
        final Timestamp timestamp = res.getTimestamp("created");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return new Post(id, author, forum, message, parent, thread, isEdited, dateFormat.format(timestamp.getTime()));
    };
}
