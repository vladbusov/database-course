package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.ForumDao;
import com.database.mailru.dockerspringboot.dao.UserDao;
import com.database.mailru.dockerspringboot.models.Forum;
import com.database.mailru.dockerspringboot.models.Message;
import com.database.mailru.dockerspringboot.models.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class ForumController {

    private UserDao userDao;
    private ForumDao forumDao;

    public ForumController(UserDao userDao, ForumDao forumDao) {
        this.userDao = userDao;
        this.forumDao = forumDao;
    }

    @PostMapping(value = "/api/forum/create", produces = "application/json")
    public Object getUserProfile(@RequestBody Forum forum , HttpServletResponse response) {
        User curUser = userDao.getByNickname(forum.getUser());
        if (curUser == null) {
            response.setStatus(404);
            return new Message("Can't find user with nickname " + forum.getUser());
        }
        try {
            List<Forum> forums = forumDao.equalForumSlug(forum.getSlug());
            if (!forums.isEmpty()) {
                response.setStatus(409);
                return forums.get(0);
            }
        } catch (NullPointerException e) {

        }
        response.setStatus(201);
        forum.setUser(curUser.getNickname());
        return forumDao.createForum(forum);
    }

}
