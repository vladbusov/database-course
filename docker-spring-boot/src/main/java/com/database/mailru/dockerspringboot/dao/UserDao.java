package com.database.mailru.dockerspringboot.dao;


import com.database.mailru.dockerspringboot.mapper.ThreadMapper;
import com.database.mailru.dockerspringboot.mapper.UserMapper;
import com.database.mailru.dockerspringboot.models.User;
import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserDao {

    private final JdbcTemplate template;
    private static Integer numOfusers;

    static {
        numOfusers = 0;
    }

    public UserDao(JdbcTemplate template) {
        this.template = template;
        this.numOfusers = 0;
    }

    public List<User> equalUsers(User user) throws  JDBCException {
        final String sql = "SELECT * FROM Users WHERE lower(email) = lower(?) OR lower(nickname) = lower(?)";
        final List<User> result =  template.query(sql, ps -> {
            ps.setString(1,user.getEmail());
            ps.setString(2,user.getNickname());
        } , UserMapper.USER_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    public List<User> equalUsersEmail(User user) throws  JDBCException {
        final String sql = "SELECT * FROM Users WHERE lower(email) = lower(?)";
        final List<User> result =  template.query(sql, ps -> {
            ps.setString(1,user.getEmail());
        } , UserMapper.USER_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


    public User createUser(User user) throws JDBCException {
        final String sql = "INSERT INTO Users (nickname, email, fullname, about) VALUES (?,?,?,?)";
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    sql ,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getNickname());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getFullname());
            pst.setObject(4, user.getAbout());
            return pst;
        });
        this.numOfusers++;
        return new User( user.getNickname(), user.getFullname(), user.getEmail(), user.getAbout() );
    }

    public User getByNickname(String nickname) {
        final String sql = "SELECT * FROM Users WHERE lower(nickname) = lower(?)";
        final List<User> result = template.query(sql, ps -> ps.setString(1, nickname), UserMapper.USER_MAPPER);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public List<User> getUsersByForum(String slug, Integer limit, String since, Boolean desc) {
        final StringBuilder sqlCreate = new StringBuilder();
        final List<Object> params = new ArrayList<>();
        if (desc == null) {
            desc = false;
        }

        sqlCreate.append("Select * from (Select distinct on (users.nickname) users.nickname, users.email, " +
                " users.fullname, users.about from users left join " +
                " posts on users.nickname = posts.author left " +
                " join thread on users.nickname = thread.author " +
                " where lower(posts.forum) = lower(?) or lower(thread.forum) = lower(?) ) a ");
        params.add(slug);
        params.add(slug);

        if (since != null) {
            if (Objects.equals(desc, Boolean.TRUE)) {
                sqlCreate.append(" where lower(a.nickname) < lower(?) ");
            } else {
                sqlCreate.append(" where lower(a.nickname) > lower(?) ");
            }
            params.add(since);
        }


        sqlCreate.append(" ORDER BY lower(a.nickname) ");

        sqlCreate.append(Objects.equals(desc, Boolean.TRUE) ? " DESC " : "ASC");


        if (limit != null) {
            sqlCreate.append(" LIMIT ? ");
            params.add(limit);
        }

        return template.query(sqlCreate.toString(), UserMapper.USER_MAPPER , params.toArray());
    }


    public void delete(int id) {
        final String sql = "DELETE FROM Users WHERE id=?";
        template.update(sql, id);
    }

    public int updateUser(User user) {
        final String sql = "UPDATE Users SET about=?, email=?, fullname=? WHERE lower(nickname) = lower(?)";
        return template.update(sql, user.getAbout(), user.getEmail(), user.getFullname(), user.getNickname());
    }

    public static Integer getNumOfusers() {
        return numOfusers;
    }

    public void clean() {
        final String sql = "DELETE FROM users";
        this.numOfusers = 0;
        template.execute(sql);
    }
}
