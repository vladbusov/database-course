package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.Forum;
import org.springframework.jdbc.core.RowMapper;

public class ForumMapper {
    public static final RowMapper<Forum> FORUM_MAPPER = (res, num) -> {
        String title = res.getString("title");
        String slug = res.getString("slug");
        String user = res.getString("userRef");
        Integer posts = res.getInt("posts");
        Integer threads = res.getInt("threads");

        return new Forum(slug,title,user,posts,threads);
    };
}
