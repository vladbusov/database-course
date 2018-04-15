package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.mapper.ThreadMapper;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import org.h2.api.TimestampWithTimeZone;
import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class ThreadDao {

    private final JdbcTemplate template;
    private final ForumDao forumDao;
    private static Integer numOfThreads;

    static {
        numOfThreads = 0;
    }

    public ThreadDao(JdbcTemplate template, ForumDao forumDao) {
        this.template = template;
        this.forumDao = forumDao;
        this.numOfThreads = 0;
    }

    public ThreadModel createThread(ThreadModel thread) throws JDBCException{

        final StringBuilder sqlCreate = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        sqlCreate.append("INSERT INTO Thread (author, ");
        params.add(thread.getAuthor());

        if (thread.getCreated() != null) {
            sqlCreate.append("created, ");
        }

        sqlCreate.append("forum, slug, message, title) VALUES(?, ");

        if (thread.getCreated() != null) {
            sqlCreate.append( "'" + thread.getCreated() + "'::timestamptz" + ", ");
        }

        sqlCreate.append("?, ?, ?, ?) RETURNING id");
        params.add(thread.getForum());
        params.add(thread.getSlug());
        params.add(thread.getMessage());
        params.add(thread.getTitle());

        final Integer id = template.queryForObject(sqlCreate.toString(), Integer.class, params.toArray());
        forumDao.incrementThreads(thread.getForum());
        this.numOfThreads++;

        return getThreadById(Long.valueOf(id));
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
        final StringBuilder sqlCreate = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        sqlCreate.append("SELECT * FROM thread WHERE lower(forum) = lower(?)");
        params.add(slug);

        if (since != null) {
            if (Objects.equals(desc, Boolean.TRUE)) {
                sqlCreate.append("and created <= ?::timestamptz ");
            } else {
                sqlCreate.append("and created >= ?::timestamptz ");
            }
            params.add(since);
        }

        sqlCreate.append("ORDER BY created ");

        sqlCreate.append(Objects.equals(desc, Boolean.TRUE) ? " DESC " : "");

        if (limit != null) {
            sqlCreate.append("LIMIT ?");
            params.add(limit);
        }

        return template.query(sqlCreate.toString(), ThreadMapper.THREAD_MAPPER, params.toArray());
    }

    public ThreadModel getThreadById(Long id) throws  JDBCException {
        final String sql = "SELECT * FROM Thread WHERE id = ?";
        final List<ThreadModel> result =  template.query(sql, ps -> {
            ps.setLong(1,id);
        } , ThreadMapper.THREAD_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public int updateThread(ThreadModel thread) {
        final String sql = "UPDATE Thread SET message=?, title=?, votes=? WHERE id=?";
        return template.update(sql, thread.getMessage(), thread.getTitle(), thread.getVotes(), thread.getId());
    }

    public void incrementVotes(Long id) {
        final ThreadModel thread =  getThreadById(id);
        if (thread == null) {
            return;
        }
        thread.setVotes(thread.getVotes() + 1);
        updateThread(thread);
    }

    public static Integer getNumOfThreads() {
        return numOfThreads;
    }

    public void clean() {
        final String sql = "DELETE FROM thread";
        this.numOfThreads = 0;
        template.execute(sql);
    }
}
