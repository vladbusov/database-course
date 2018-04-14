package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.*;
import com.database.mailru.dockerspringboot.models.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
public class ServiceController {

    private UserDao userDao;
    private ForumDao forumDao;
    private ThreadDao threadDao;
    private PostDao postDao;
    private VoteDao voteDao;

    public ServiceController(UserDao userDao, ForumDao forumDao, ThreadDao threadDao, PostDao postDao, VoteDao voteDao) {
        this.userDao = userDao;
        this.forumDao = forumDao;
        this.threadDao = threadDao;
        this.postDao = postDao;
        this.voteDao = voteDao;
    }

    @GetMapping(value = "/api/service/status", produces = "application/json")
    public Object getServiceStatus(HttpServletResponse response) {
        HashMap<String,Integer> hashMap = new HashMap();
        hashMap.put("forum", ForumDao.getNumOfForums());
        hashMap.put("post", PostDao.getNumOfPosts());
        hashMap.put("thread", ThreadDao.getNumOfThreads());
        hashMap.put("user", UserDao.getNumOfusers());
        response.setStatus(200);
        return hashMap;
    }

    @PostMapping(value = "/api/service/clear", produces = "application/json")
    public Object cleanService(HttpServletResponse response) {
        userDao.clean();
        forumDao.clean();
        threadDao.clean();
        postDao.clean();
        voteDao.clean();
        response.setStatus(200);
        return new Message("Successful cleaning");
    }

}
