package com.database.mailru.dockerspringboot.mapper;

import com.database.mailru.dockerspringboot.models.Vote;
import org.springframework.jdbc.core.RowMapper;
public class VoteMapper {
    public static final RowMapper<Vote> VOTE_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        String nickname = res.getString("nickname");
        Integer voice = res.getInt("voice");
        Long threadId = res.getLong("threadId");
        return new Vote(id,nickname,voice, threadId);
    };

}
