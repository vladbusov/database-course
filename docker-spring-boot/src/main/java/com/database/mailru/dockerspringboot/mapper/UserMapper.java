package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.User;
import org.springframework.jdbc.core.RowMapper;

public class UserMapper {
    public static final RowMapper<User> USER_MAPPER = (res, num) -> {
        String nickname = res.getString("nickname");
        String email = res.getString("email");
        String fullname = res.getString("fullname");
        String about = res.getString("about");

        return new User( nickname, fullname, email,  about);
    };
}