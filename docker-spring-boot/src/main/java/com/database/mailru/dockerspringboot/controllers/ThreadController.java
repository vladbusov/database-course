package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.ForumDao;
import com.database.mailru.dockerspringboot.dao.PostDao;
import com.database.mailru.dockerspringboot.dao.ThreadDao;
import com.database.mailru.dockerspringboot.dao.UserDao;
import com.database.mailru.dockerspringboot.models.Forum;
import com.database.mailru.dockerspringboot.models.Message;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class ThreadController {

    private UserDao userDao;
    private ForumDao forumDao;
    private ThreadDao threadDao;
    private PostDao postDao;

    public ThreadController(UserDao userDao, ForumDao forumDao, ThreadDao threadDao, PostDao postDao) {
        this.userDao = userDao;
        this.forumDao = forumDao;
        this.threadDao = threadDao;
        this.postDao = postDao;
    }

    @PostMapping(value = "/api/forum/{slug}/create", produces = "application/json")
    public Object createUser(@PathVariable("slug") String slug, @RequestBody ThreadModel threadModel, HttpServletResponse response) throws IOException, ParseException {
        if (forumDao.getForumForSlug(slug) == null) {
            response.setStatus(404);
            return new Message("Can't find forum slug" + slug );
        }
        if (userDao.getByNickname(threadModel.getAuthor()) == null) {
            response.setStatus(404);
            return new Message("Can't find forum user" + threadModel.getAuthor() );
        }
        threadModel.setForum(slug);
        try {
            List<ThreadModel> threads = threadDao.equalThread(threadModel.getId().intValue());
        } catch (NullPointerException e) {
            response.setStatus(201);
            forumDao.incrementThreads(threadModel.getSlug());
            return threadDao.createThread(threadModel);
        }

        List<ThreadModel> thread = threadDao.equalThread(threadModel.getId().intValue());
        thread = threadDao.equalThread(threadModel.getId().intValue());
        response.setStatus(409);
        return thread.get(0);

    }

    @GetMapping(value = "/api/forum/{slug}/details", produces = "application/json")
    public Object getForumDetails(@PathVariable("slug") String slug, HttpServletResponse response) {
        List<Forum> forums = forumDao.equalForumSlug(slug) ;
        try {
            if (!forums.isEmpty()) {
                response.setStatus(200);
                return forums.get(0);
            }
        } catch (NullPointerException e) {

        }
        response.setStatus(404);
        return new Message("Can't find forum slug with " + slug);
    }

    @GetMapping(value = "/api/forum/{slug}/threads", produces = "application/json")
    public Object getForumThreads(@PathVariable("slug") String slug,
                                  @RequestParam( value = "limit", required = false) Integer limit,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "desc", required = false) Boolean desc ,
                                  HttpServletResponse response) {
        List<Forum> forums = forumDao.equalForumSlug(slug) ;
        try {
            if (forums.isEmpty()) {
                response.setStatus(404);
                return new Message("Can't find forum slug with " + slug);
            }
        } catch (NullPointerException ignored){
        }
        if (forumDao.getForumForSlug(slug) == null) {
            response.setStatus(404);
            return new Message("Can't find any threads");
        }
        response.setStatus(200);
        return threadDao.getThreadsForForum(slug,limit,since,desc);
    }

    @GetMapping(value = "/api/forum/{slug}/users", produces = "application/json")
    public Object getForumUsers(@PathVariable("slug") String slug,
                                  @RequestParam( value = "limit", required = false) Integer limit,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "desc", required = false) Boolean desc ,
                                  HttpServletResponse response) {
        List<Forum> forums = forumDao.equalForumSlug(slug) ;
        try {
            if (forums.isEmpty()) {
                response.setStatus(404);
                return new Message("Can't find forum slug with " + slug);
            }
        } catch (NullPointerException e){
        }
        response.setStatus(200);
        return userDao.getUsersByForum(slug,limit,since,desc);
    }

    @GetMapping(value = "/api/thread/{slug_or_id}/posts", produces = "application/json")
    public Object getThreadPosts(@PathVariable("slug_or_id") Long id,
                                @RequestParam( value = "limit", required = false) Integer limit,
                                @RequestParam(value = "since", required = false) String since,
                                @RequestParam(value = "desc", required = false) Boolean desc ,
                                 @RequestParam(value = "sort", required = false) String sort,
                                HttpServletResponse response) {
        ThreadModel threadModel = threadDao.getThreadById(id);
        try {
            if ( threadModel == null){
                response.setStatus(404);
                return new Message("Can't find thread with id = " + id);
            }
        } catch (NullPointerException e){
        }
        response.setStatus(200);
        return postDao.getPostsForThread(id,limit,since,sort,desc);
    }



}
