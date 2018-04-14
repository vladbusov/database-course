package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.Forum;
import com.database.mailru.dockerspringboot.models.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

public class PostMapper {
    public static final RowMapper<Post> POST_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        String author = res.getString("author");
        String forum = res.getString("forum");
        String message = res.getString("message");
        Long parent = res.getLong("parent");
        Long thread = res.getLong("thread");
        Boolean isEdited = res.getBoolean("isEdited");
        Timestamp created = res.getTimestamp("created");

        return new Post(id, author, forum, message, parent, thread, isEdited, created);
    };
}
