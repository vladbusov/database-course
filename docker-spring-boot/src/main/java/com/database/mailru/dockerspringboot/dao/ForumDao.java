package com.database.mailru.dockerspringboot.dao;

import com.database.mailru.dockerspringboot.mapper.ForumMapper;
import com.database.mailru.dockerspringboot.mapper.UserMapper;
import com.database.mailru.dockerspringboot.models.Forum;
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
public class ForumDao {

    private final JdbcTemplate template;

    public ForumDao(JdbcTemplate template) {
        this.template = template;
    }

    public Forum createForum(Forum forum) {
        final String sql = "INSERT INTO forum (slug,title,user,posts,threads) VALUES (?,?,?,0,0)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    sql + " returning id",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, forum.getSlug() );
            pst.setString(2, forum.getTitle() );
            pst.setString(3, forum.getUser() );
            return pst;
        }, keyHolder);
        return new Forum( keyHolder.getKey().longValue(),forum.getSlug(), forum.getTitle(), forum.getUser());
    }

    public Forum getForumForSlug(String slug) {
        final String sql = "SELECT * FROM forum WHERE slug= ?";
        final List<Forum> result = template.query(sql, ps -> ps.setString(1, slug), ForumMapper.FORUM_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public void incrementThreads(String slug) {
        final Forum forum = getForumForSlug(slug);
        if (forum != null) {
            final String sql = "UPDATE forum SET threads=? WHERE slug=?";
            template.update(sql, forum.getThreads() + 1, slug);
        }
    }

    public List<Forum> equalForumSlug(String slug) throws JDBCException {
        final String sql = "SELECT * FROM forum WHERE slug = ?";
        return  template.query(sql, ps -> {
            ps.setString(1,slug);
        } , ForumMapper.FORUM_MAPPER);
    }


}
