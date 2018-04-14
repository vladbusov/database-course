package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.UserDao;
import com.database.mailru.dockerspringboot.models.Message;
import com.database.mailru.dockerspringboot.models.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    private UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @PostMapping(value = "/api/user/{nickname}/create", produces = "application/json")
    public Object createUser(@PathVariable("nickname") String nickname, @RequestBody User user ,HttpServletResponse response) throws IOException {
        List<User> list = userDao.equalUsers(user);
        if (!list.isEmpty()) {
            response.setStatus(409);
            return list;
        }
        user.setNickname(nickname);
        User result = userDao.createUser(user);
        if (result != null) {
            response.setStatus(201);
            return result;
        }
        return null;
    }

    @GetMapping(value = "/api/user/{nickname}/profile", produces = "application/json")
    public Object getUserProfile(@PathVariable(value = "nickname") String nickname, HttpServletResponse response) {
        User result = userDao.getByNickname(nickname);
        if (result == null) {
            response.setStatus(404);
            return new Message("Can't find user with nickname " + nickname);
        }
        response.setStatus(200);
        return result;
    }

    @PostMapping(value = "/api/user/{nickname}/profile", produces = "application/json")
    public Object editUserProfile(@PathVariable("nickname") String nickname, @RequestBody User user ,HttpServletResponse response) throws IOException {
        if (userDao.getByNickname(nickname) == null) {
            response.setStatus(404);
            return new Message("Can't find user with nickname " + nickname);
        }

        if (!userDao.equalUsersEmail(user).isEmpty()) {
            response.setStatus(409);
            return new Message("User data have conflict fields");
        }
        user.setNickname(nickname);
        int result = userDao.updateUser(user);
        if (result < 1) {
            response.setStatus(404);
            return new Message("Can't find user with nickname " + nickname);
        }
        return userDao.getByNickname(nickname);

    }
}
