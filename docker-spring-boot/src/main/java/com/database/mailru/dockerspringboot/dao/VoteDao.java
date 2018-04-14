package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.models.ThreadModel;
import com.database.mailru.dockerspringboot.models.Vote;
import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Service
@Transactional
public class VoteDao {

    private final JdbcTemplate template;

    public VoteDao(JdbcTemplate template) {
        this.template = template;
    }

    public Vote createVote(Vote vote) throws JDBCException {
        final String sql = "INSERT INTO Votes (nickname, voice, threadId) VALUES (?,?,?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    sql + " returning id",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, vote.getNickname());
            pst.setInt(2, vote.getVoice());
            pst.setLong(3, vote.getThreadId());
            return pst;
        }, keyHolder);
        return new Vote(keyHolder.getKey().longValue(), vote.getNickname(), vote.getVoice(), vote.getThreadId());
    }

}
