package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.mapper.UserMapper;
import com.database.mailru.dockerspringboot.mapper.VoteMapper;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import com.database.mailru.dockerspringboot.models.User;
import com.database.mailru.dockerspringboot.models.Vote;
import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public Vote getByNicknameAndThread(String nickname, Long threadId) {
        final String sql = "SELECT * FROM votes WHERE lower(nickname) = lower(?) AND threadId = ?";
        final List<Vote> result = template.query(sql, ps -> {
            ps.setString(1, nickname);
            ps.setLong(2,threadId);
        }, VoteMapper.VOTE_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public Integer sumOfVotes(Long threadId) {
        final String sqlQuery = "SELECT SUM(voice) FROM Votes where threadId = " + threadId;
        return template.queryForObject(sqlQuery, Integer.class);
    }


    public void clean() {
        final String sql = "DELETE FROM votes";
        template.execute(sql);
    }

    public int updateVote(Vote vote) {
        final String sql = "UPDATE Votes SET voice=? WHERE lower(nickname) = lower(?) AND threadId = ?";
        return template.update(sql, vote.getVoice(), vote.getNickname(), vote.getThreadId());
    }

}
