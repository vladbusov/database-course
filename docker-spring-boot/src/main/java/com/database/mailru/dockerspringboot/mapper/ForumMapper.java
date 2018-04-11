package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.Forum;
import org.springframework.jdbc.core.RowMapper;

public class ForumMapper {
    public static final RowMapper<Forum> FORUM_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        String title = res.getString("title");
        String slug = res.getString("slug");
        String user = res.getString("user");
        Integer posts = res.getInt("posts");
        Integer threads = res.getInt("threads");

        return new Forum(id,slug,title,user,posts,threads);
    };
}
