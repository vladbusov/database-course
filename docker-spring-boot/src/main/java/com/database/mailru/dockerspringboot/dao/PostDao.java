package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.mapper.PostMapper;
import com.database.mailru.dockerspringboot.mapper.ThreadMapper;
import com.database.mailru.dockerspringboot.models.Post;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import com.database.mailru.dockerspringboot.models.User;
import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Post createPost(Post post) throws JDBCException {
        final String sql = "INSERT INTO posts (author,forum,message,parent,thread,isEdited,created) VALUES (?,?,?,?,?,?,?)";


        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
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
            pst.setTimestamp(7,post.getCreated());
            return pst;
        }, keyHolder);
        this.numOfPosts++;
        return new Post(keyHolder.getKey().longValue(), post.getAuthor(), post.getForum(), post.getMessage(), post.getParent(), post.getThread(), post.getEdited(), post.getCreated());
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
        final String sql = "UPDATE Posts SET message=? WHERE id =?";
        template.update(sql, post.getMessage(), post.getId());
    }

    public void clean() {
        final String sql = "DELETE FROM posts";
        this.numOfPosts = 0;
        template.execute(sql);
    }

    public List<Post> getPostsForThread(Long id, Integer limit, String since, String sort, Boolean desc) {

        final ThreadModel thread = threadDao.getThreadById(id);
        if (sort == null)
            sort = "flat";
        switch (sort) {
            case "tree":
                return getSqlSortTree(thread.getId(), limit, since, desc);
            case "parent_tree":
                return getSqlSortParentTree(thread.getId(), limit, since, desc);
            default:
                return getSqlSortFlat(thread.getId(), limit, since, desc);
        }


    }

    public List<Post> getSqlSortFlat(Long id, Integer limit, String since, Boolean desc) {
        // flat - по дате, комментарии выводятся простым списком в порядке создания;
        final StringBuilder sqlQuery = new StringBuilder();
        final ArrayList<Object> params = new ArrayList<>();
        sqlQuery.append("SELECT u.nickname as author, p.created, f.slug as forum, p.id, p.isEdited as isEdited, p.message, p.parent, p.thread as thread " +
                " FROM Posts p JOIN users u ON p.author = u.id JOIN forum f on p.forum = f.slug WHERE thread.Id = ? ");
        params.add(id);

        if (since != null) {
            sqlQuery.append(" AND p.id " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                sqlQuery.append(" < ? ");
            } else {
                sqlQuery.append(" > ? ");
            }

            params.add(since);
        }

        sqlQuery.append(" ORDER BY (p.id) ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            sqlQuery.append(" DESC ");
        }

        if (limit != null) {
            sqlQuery.append(" LIMIT ? ");
            params.add(limit);
        }

        return template.query(sqlQuery.toString(), PostMapper.POST_MAPPER, params.toArray());
    }

    public List<Post> getSqlSortTree(Long thread_id, Integer limit, String since, Boolean desc) {
        // tree - древовидный, комментарии выводятся отсортированные в дереве по N штук;
        final StringBuilder sqlQuery = new StringBuilder();
        final ArrayList<Object> params = new ArrayList<>();
        sqlQuery.append("SELECT u.nickname as author, p.created, f.slug as forum, p.id, p.isEdited as isEdited, p.message, p.parent, p.thread as thread " +
                " FROM Posts p JOIN users u ON p.author = u.id JOIN forum f on p.forum = f.slug WHERE thread.Id = ? ");
        params.add(thread_id);

        if (since != null) {
            sqlQuery.append(" AND p.path " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                sqlQuery.append(" < ");
            } else {
                sqlQuery.append(" > ");
            }

            sqlQuery.append(" (SELECT path from Posts Where id = ?) ");
            params.add(since);
        }

        sqlQuery.append(" ORDER BY p.path ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            sqlQuery.append(" DESC ");
        }

        if (limit != null) {
            sqlQuery.append(" LIMIT ? ");
            params.add(limit);
        }

        return template.query(sqlQuery.toString(), PostMapper.POST_MAPPER, params.toArray());
    }

    public List<Post> getSqlSortParentTree(Long thread_id, Integer limit, String since, Boolean desc) {
        // parent_tree - древовидные с пагинацией по родительским (parent_tree), на странице N
        // родительских комментов и все комментарии прикрепленные к ним, в древвидном отображение.
        final StringBuilder sqlQuery = new StringBuilder();
        final ArrayList<Object> params = new ArrayList<>();
        sqlQuery.append("SELECT u.nickname as author, p.created, f.slug as forum, p.id, p.isEdited as isEdited, p.message, p.parent, p.thread as thread " +
                " FROM Posts p JOIN users u ON p.author = u.id JOIN forums f on p.forum = f.slug " +
                " WHERE p.parent IN ( SELECT id FROM posts WHERE thread = ? AND parent = 0 ");
        params.add(thread_id);

        if (since != null) {
            sqlQuery.append(" AND id " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                sqlQuery.append(" < ");
            } else {
                sqlQuery.append(" > ");
            }

            sqlQuery.append(" (SELECT parent from Posts Where id = ?) ");
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

        sqlQuery.append(" ) ORDER BY ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            sqlQuery.append(" p.parent DESC, ");
        }
        sqlQuery.append(" p.path ");

        return template.query(sqlQuery.toString(), PostMapper.POST_MAPPER, params.toArray());
    }

}
