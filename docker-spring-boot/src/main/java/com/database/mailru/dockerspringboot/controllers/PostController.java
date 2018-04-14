package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.ForumDao;
import com.database.mailru.dockerspringboot.dao.PostDao;
import com.database.mailru.dockerspringboot.dao.ThreadDao;
import com.database.mailru.dockerspringboot.dao.UserDao;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class PostController {

    private UserDao userDao;
    private ForumDao forumDao;
    private ThreadDao threadDao;
    private PostDao postDao;

    public PostController(UserDao userDao, ForumDao forumDao, ThreadDao threadDao, PostDao postDao) {
        this.userDao = userDao;
        this.forumDao = forumDao;
        this.threadDao = threadDao;
        this.postDao = postDao;
    }

    @PostMapping(value = "/api/thread/{slug_or_id}/create", produces = "application/json")
    public Object createPostd(@PathVariable("slug_or_id") String id, HttpServletResponse response) {
        return null;
    }


}
