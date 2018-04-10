package com.database.mailru.dockerspringboot.dao;


import org.hibernate.JDBCException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class UserDao {

    private final JdbcTemplate template;

    public UserDao(JdbcTemplate template) {
        this.template = template;
    }

    public void save() throws JDBCException {
        final String sql = "INSERT INTO Users (nickname, email, password) VALUES (?,?,?)";
        template.update(sql, null, null, null);
    }

    public String getById(Long id) {
        final String sql = "SELECT * FROM Users WHERE id = ?";
        return null;
    }


    public void update() {
        final String sql = "UPDATE Users SET nickname=?, email=?, password=? WHERE id=?";
        template.update(sql, null, null, null, null);
    }

    public void delete(int id) {
        final String sql = "DELETE FROM Users WHERE id=?";
        template.update(sql, id);
    }

}
