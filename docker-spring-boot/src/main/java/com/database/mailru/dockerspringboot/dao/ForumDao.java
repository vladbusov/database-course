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
    private static Integer numOfForums;


    public ForumDao(JdbcTemplate template) {
        this.template = template;
    }

    static {
        numOfForums = 0;
    }

    public Forum createForum(Forum forum) {
        final String sql = "INSERT INTO forum (slug,title,userRef,posts,threads) VALUES (?,?,?,0,0)";
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    sql,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, forum.getSlug() );
            pst.setString(2, forum.getTitle() );
            pst.setString(3, forum.getUser() );
            return pst;
        });
        this.numOfForums++;
        return new Forum( forum.getSlug(), forum.getTitle(), forum.getUser());
    }

    public Forum getForumForSlug(String slug) {
        final String sql = "SELECT * FROM forum WHERE lower(slug)= lower(?)";
        final List<Forum> result = template.query(sql, ps -> ps.setString(1, slug), ForumMapper.FORUM_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public void incrementThreads(String slug) {
        final Forum forum = getForumForSlug(slug);
        if (forum != null) {
            final String sql = "UPDATE forum SET threads=? WHERE lower(slug)=lower(?)";
            template.update(sql, forum.getThreads() + 1, slug);
        }
    }

    public void updateForumInfo(String slug, Integer countThreads, Integer countPosts) {
        final String sql = "UPDATE Forum SET threads=?, posts=? WHERE lower(slug) = lower(?)";
        template.update(sql, countThreads, countPosts, slug );
    }

    public List<Forum> equalForumSlug(String slug) throws JDBCException {
        final String sql = "SELECT * FROM forum WHERE lower(slug) = lower(?)";
        final List<Forum> result = template.query(sql, ps -> {
            ps.setString(1,slug);
        } , ForumMapper.FORUM_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


    public static Integer getNumOfForums() {
        return numOfForums;
    }

    public void clean() {
        final String sql = "DELETE FROM forum";
        template.execute(sql);
        this.numOfForums = 0;
    }
}
