package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.mapper.ForumMapper;
import com.database.mailru.dockerspringboot.mapper.ThreadMapper;
import com.database.mailru.dockerspringboot.models.Forum;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import com.database.mailru.dockerspringboot.models.User;
import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.util.List;

@Transactional
@Service
public class ThreadDao {

    private final JdbcTemplate template;
    private final ForumDao forumDao;

    public ThreadDao(JdbcTemplate template, ForumDao forumDao) {
        this.template = template;
        this.forumDao = forumDao;
    }

    public ThreadModel createThread(ThreadModel thread) throws JDBCException {
        final String sql = "INSERT INTO thread (slug,author,created,message,title,forum,votes) VALUES (?,?,?,?,?,?,?)";

        final String forum = forumDao.getForumForSlug(thread.getSlug()).getTitle();
        final Integer votes = 0;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    sql + " returning id",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, thread.getSlug());
            pst.setString(2, thread.getAuthor());
            pst.setTimestamp(3, thread.getCreated());
            pst.setString(4,thread.getMessage());
            pst.setString(5,thread.getTitle());
            pst.setString(6,forum);
            pst.setInt(7,votes);
            return pst;
        }, keyHolder);
        return new ThreadModel(keyHolder.getKey().longValue(),thread.getSlug(),thread.getAuthor(), forum, thread.getCreated(), thread.getMessage(), thread.getTitle(),votes);
    }

    public List<ThreadModel> equalThread(int id) throws JDBCException {
        final String sql = "SELECT * FROM thread WHERE id = ?";
        final List<ThreadModel> result = template.query(sql, ps -> { ps.setInt(1,id); } , ThreadMapper.THREAD_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return   result;
    }

    public List<ThreadModel> getThreadsForForum(String slug, Integer limit, String since, Boolean desc) {
        String sql = "SELECT * FROM thread WHERE slug = ? AND created > ? ORDER BY created";
        if (desc) {
            sql = sql  + " DESC";
        }
        sql = sql + " LIMIT ?";
        final List<ThreadModel> result =  template.query(sql, ps -> {
            ps.setString(1, slug);
            ps.setString(2, since);
            ps.setInt(3,limit);
        } , ThreadMapper.THREAD_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return   result;
    }

}
