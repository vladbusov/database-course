package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.*;
import com.database.mailru.dockerspringboot.models.Message;
import com.database.mailru.dockerspringboot.models.Post;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import com.database.mailru.dockerspringboot.models.Vote;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

@RestController
public class PostController {

    private UserDao userDao;
    private ForumDao forumDao;
    private ThreadDao threadDao;
    private PostDao postDao;
    private VoteDao voteDao;

    public PostController(UserDao userDao, ForumDao forumDao, ThreadDao threadDao, PostDao postDao, VoteDao voteDao) {
        this.userDao = userDao;
        this.forumDao = forumDao;
        this.threadDao = threadDao;
        this.postDao = postDao;
        this.voteDao = voteDao;
    }

    @PostMapping(value = "/api/thread/{slug_or_id}/create", produces = "application/json")
    public Object createPost(@PathVariable("slug_or_id") Long id, @RequestBody Post post, HttpServletResponse response) {
        post.setCreated(new Timestamp(System.currentTimeMillis()));
        post.setEdited(false);

        post.setThread(id);
        if (threadDao.getThreadById(id) == null ) {
            response.setStatus(404);
            return  new Message("Can't find thread with id = " + id);
        }
        post.setForum(threadDao.getThreadById(id).getForum());


        if (postDao.getPostById(post.getParent()) == null && post.getParent() != 0L) {
            response.setStatus(409);
            return  new Message("Can't find parent post with id = " + post.getParent());
        }

        response.setStatus(201);
        return postDao.createPost(post);
    }

    @GetMapping(value = "/api/thread/{slug_or_id}/details", produces = "application/json")
    public Object getThreadInfo(@PathVariable("slug_or_id") Long id,HttpServletResponse response) {
        ThreadModel threadModel = threadDao.getThreadById(id);
        if (threadModel == null) {
            response.setStatus(404);
            return  new Message("Can't find thread with id = " + id);
        }
        response.setStatus(200);
        return threadModel;
    }

    @PostMapping(value = "/api/thread/{slug_or_id}/details", produces = "application/json")
    public Object updateThreadInfo(@PathVariable("slug_or_id") Long id,  @RequestBody ThreadModel thread, HttpServletResponse response) {
        ThreadModel threadModel = threadDao.getThreadById(id);
        if (threadModel == null) {
            response.setStatus(404);
            return  new Message("Can't find thread with id = " + id);
        }
        thread.setId(id);
        response.setStatus(200);
        return threadDao.updateThread(thread);
    }

    @PostMapping(value = "/api/thread/{slug_or_id}/vote", produces = "application/json")
    public Object updateThreadInfo(@PathVariable("slug_or_id") Long id, @RequestBody Vote vote, HttpServletResponse response) {
        ThreadModel threadModel = threadDao.getThreadById(id);
        if (threadModel == null) {
            response.setStatus(404);
            return  new Message("Can't find thread with id = " + id);
        }
        vote.setThreadId(id);
        threadDao.incrementVotes(id);
        response.setStatus(200);
        return voteDao.createVote(vote);
    }

    @GetMapping(value = "/api/post/{id}/details", produces = "application/json")
    public Object getPostInf(@PathVariable("id") Long id, HttpServletResponse response ) {
        HashMap<String,Object> hashMap = new HashMap<>();
        Post curPost = postDao.getPostById(id);
        if (curPost == null) {
            response.setStatus(404);
            return  new Message("Can't find thread with id = " + id);
        }
        hashMap.put("author", userDao.getByNickname(curPost.getAuthor()));
        hashMap.put("forum", forumDao.getForumForSlug(curPost.getForum()));
        hashMap.put("post", curPost);
        hashMap.put("thread", threadDao.getThreadById(curPost.getThread()));
        response.setStatus(200);
        return hashMap;
    }

    @PostMapping(value = "/api/post/{id}/details", produces = "application/json")
    public Object changeMessage(@PathVariable("id") Long id,@RequestBody Post post, HttpServletResponse response ) {
        Post curPost = postDao.getPostById(id);
        if (curPost == null) {
            response.setStatus(404);
            return  new Message("Can't find thread with id = " + id);
        }
        curPost.setMessage(post.getMessage());
        response.setStatus(200);
        postDao.updatePost(curPost);
        return curPost;
    }

}
