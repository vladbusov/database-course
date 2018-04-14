package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.mapper.PostMapper;
import com.database.mailru.dockerspringboot.mapper.ThreadMapper;
import com.database.mailru.dockerspringboot.models.Post;
import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.util.List;

@Transactional
@Service
public class PostDao {
    private final JdbcTemplate template;
    private final ForumDao forumDao;
    private final ThreadDao threadDao;
    private final UserDao userDao;

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
}
