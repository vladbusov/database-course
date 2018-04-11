package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.ForumDao;
import com.database.mailru.dockerspringboot.dao.ThreadDao;
import com.database.mailru.dockerspringboot.dao.UserDao;
import com.database.mailru.dockerspringboot.models.Forum;
import com.database.mailru.dockerspringboot.models.Message;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class ThreadController {

    private UserDao userDao;
    private ForumDao forumDao;
    private ThreadDao threadDao;

    public ThreadController(UserDao userDao, ForumDao forumDao, ThreadDao threadDao) {
        this.userDao = userDao;
        this.forumDao = forumDao;
        this.threadDao = threadDao;
    }

    @PostMapping(value = "/forum/{slug}/create", produces = "application/json")
    public Object createUser(@PathVariable("slug") String slug, @RequestBody ThreadModel threadModel, HttpServletResponse response) throws IOException {
        if (forumDao.getForumForSlug(slug) == null) {
            response.setStatus(404);
            return new Message("Can't find forum slug" + slug );
        }
        if (userDao.getByNickname(threadModel.getAuthor()) == null) {
            response.setStatus(404);
            return new Message("Can't find forum user" + threadModel.getAuthor() );
        }
        threadModel.setSlug(slug);
        List<ThreadModel> threads = threadDao.equalThread(threadModel.getId().intValue());
        if ( !threads.isEmpty() ) {
            response.setStatus(409);
            return threads.get(0);
        }
        response.setStatus(201);
        forumDao.incrementThreads(threadModel.getSlug());
        return threadDao.createThread(threadModel);
    }

    @GetMapping(value = "/forum/{slug}/details", produces = "application/json")
    public Object getForumDetails(@PathVariable("slug") String slug, HttpServletResponse response) {
        List<Forum> forums = forumDao.equalForumSlug(slug) ;
        if ( !forums.isEmpty() ) {
            response.setStatus(200);
            return forums.get(0);
        }
        response.setStatus(404);
        return new Message("Can't find forum slug with " + slug);
    }

    @GetMapping(value = "/forum/{slug}/threads", produces = "application/json")
    public Object getForumThreads(@PathVariable("slug") String slug, @RequestParam("limit") Integer limit, @RequestParam("since") String since, @RequestParam("desc") Boolean desc , HttpServletResponse response) {
        List<Forum> forums = forumDao.equalForumSlug(slug) ;
        if ( forums.isEmpty() ) {
            response.setStatus(404);
            return new Message("Can't find forum slug with " + slug);
        }
        response.setStatus(200);
        return threadDao.getThreadsForForum(slug,limit,since,desc);
    }


}
