package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.mapper.PostMapper;
import com.database.mailru.dockerspringboot.mapper.ThreadMapper;
import com.database.mailru.dockerspringboot.models.Post;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import com.database.mailru.dockerspringboot.models.User;
import org.flywaydb.core.internal.database.postgresql.PostgreSQLConnection;
import org.hibernate.JDBCException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Transactional
@Service
public class PostDao {
    private final JdbcTemplate template;
    private final ForumDao forumDao;
    private final ThreadDao threadDao;
    private final UserDao userDao;
    private static Integer numOfPosts;

    static {
        numOfPosts = 0;
    }

    public PostDao(JdbcTemplate template, ForumDao forumDao, ThreadDao threadDao, UserDao userDao) {
        this.template = template;
        this.forumDao = forumDao;
        this.threadDao = threadDao;
        this.userDao = userDao;
    }

    public Post createPost(Post post, String time) throws JDBCException, SQLException {
        final String sql = "INSERT INTO posts (author,forum,message,parent,thread,isEdited,created,path) VALUES (?,?,?,?,?,?,?::TIMESTAMPTZ,?)";

        Long parentId = post.getParent();
        Post parent = getPostById(parentId);


        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String finalNewPath = "";
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    sql + " returning id",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, post.getAuthor());
            pst.setString(2, post.getForum());
            pst.setString(3, post.getMessage());
            pst.setLong(4, post.getParent());
            pst.setLong(5, post.getThread());
            pst.setBoolean(6, post.getEdited());
            pst.setString(7, time);
            pst.setString(8, finalNewPath);
            return pst;
        }, keyHolder);
        this.numOfPosts++;

        String newPath = "";
        if (parent != null) {
            newPath = parent.getPath() + "." + String.valueOf(keyHolder.getKey().longValue());
        } else {
            newPath = "0." + String.valueOf(keyHolder.getKey().longValue());
        }

        post.setId(keyHolder.getKey().longValue());
        post.setPath(newPath);
        updatePost(post);
        return (getPostById(keyHolder.getKey().longValue()));
    }



    public Post getPostById(Long id) throws  JDBCException {
        final String sql = "SELECT * FROM Posts WHERE id = ?";
        final List<Post> result =  template.query(sql, ps -> {
            ps.setLong(1,id);
        } , PostMapper.POST_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public static Integer getNumOfPosts() {
        return numOfPosts;
    }

    public void updatePost(Post post) {
        final String sql = "UPDATE Posts SET message=?, path =? WHERE id =?";
        template.update(sql, post.getMessage(),post.getPath(), post.getId());
    }

    public void clean() {
        final String sql = "DELETE FROM posts";
        this.numOfPosts = 0;
        template.execute(sql);
    }

    public List<Post> getPostsForThread(Long id, Integer limit, String since, String sort, Boolean desc) {

        final ThreadModel thread = threadDao.getThreadById(id);
        if (sort == null) {
            sort = "flat";
        }
        switch (sort) {
            case "tree":
                return sortByTree(thread.getId(), limit, since, desc);
            case "parent_tree":
                return sortByParentTree(thread.getId(), limit, since, desc);
            case "flat":
                return sortByFlat(thread.getId(), limit, since, desc);
        }
        return null;
    }

    public Integer countOfPosts(String forum) {
        final String sqlQuery = "SELECT count(id) FROM Posts where forum = '" + forum + "'";
        return template.queryForObject(sqlQuery, Integer.class);
    }

    public List<Post> sortByFlat(Long id, Integer limit, String since, Boolean desc) {
        // flat - по дате, комментарии выводятся простым списком в порядке создания;
        final StringBuilder sqlQuery = new StringBuilder();
        final ArrayList<Object> params = new ArrayList<>();
        sqlQuery.append("select * from posts where thread = ?");
        params.add(id);

        if (since != null) {
            sqlQuery.append(" AND id " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                sqlQuery.append(" < (select id from posts where id = ?::integer) ");
            } else {
                sqlQuery.append(" > (select id from posts where id = ?::integer) ");
            }

            params.add(since);
        }

        sqlQuery.append(" ORDER BY id ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            sqlQuery.append(" DESC ");
        }

        if (limit != null) {
            sqlQuery.append(" LIMIT ? ");
            params.add(limit);
        }

        return template.query(sqlQuery.toString(), PostMapper.POST_MAPPER, params.toArray());
    }

    public List<Post> sortByTree(Long thread_id, Integer limit, String since, Boolean desc) {
        // tree - древовидный, комментарии выводятся отсортированные в дереве по N штук;
        final StringBuilder sqlQuery = new StringBuilder();
        final ArrayList<Object> params = new ArrayList<>();
        sqlQuery.append("select * from posts where thread = ?");
        params.add(thread_id);

        if (since != null) {
            sqlQuery.append(" AND path " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                sqlQuery.append(" < ");
            } else {
                sqlQuery.append(" > ");
            }

            sqlQuery.append(" (SELECT path from Posts Where id = ?::integer) ");
            params.add(since);
        }

        sqlQuery.append(" ORDER BY path ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            sqlQuery.append(" DESC ");
        }

        if (limit != null) {
            sqlQuery.append(" LIMIT ? ");
            params.add(limit);
        }

        return template.query(sqlQuery.toString(), PostMapper.POST_MAPPER, params.toArray());
    }

    public List<Post> sortByParentTree(Long thread_id, Integer limit, String since, Boolean desc) {
        // parent_tree - древовидные с пагинацией по родительским (parent_tree), на странице N
        // родительских комментов и все комментарии прикрепленные к ним, в древвидном отображение.
        final StringBuilder sqlQuery = new StringBuilder();
        final ArrayList<Object> params = new ArrayList<>();
        sqlQuery.append("select * from posts where (string_to_array(path,'.'))[2]::integer in  " +
                "(select id from posts where thread = ? and parent = 0 ");
        params.add(thread_id);

        if (since != null) {
            sqlQuery.append(" AND id " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                sqlQuery.append(" < ");
            } else {
                sqlQuery.append(" > ");
            }

            sqlQuery.append(" (SELECT (string_to_array(path,'.'))[2]::integer from Posts Where id = ?::integer)::integer ");
            params.add(since);
        }


        sqlQuery.append(" ORDER BY id ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            sqlQuery.append("  DESC ");
        }

        if (limit != null) {
            sqlQuery.append(" LIMIT ? ");
            params.add(limit);
        }

        // string_to_array(path,'.');
        sqlQuery.append(") ORDER BY  ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            sqlQuery.append(" (string_to_array(path,'.'))[2] DESC, path ASC ");
        } else {
            sqlQuery.append(" path ");
        }



        return template.query(sqlQuery.toString(), PostMapper.POST_MAPPER, params.toArray());
    }

}
