package com.database.mailru.dockerspringboot.controllers;

import com.database.mailru.dockerspringboot.dao.*;
import com.database.mailru.dockerspringboot.models.Message;
import com.database.mailru.dockerspringboot.models.Post;
import com.database.mailru.dockerspringboot.models.ThreadModel;
import com.database.mailru.dockerspringboot.models.Vote;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    public Object createPost(@PathVariable("slug_or_id") String id, @RequestBody Post[] posts, HttpServletResponse response) throws SQLException {
        final String currentTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        for (Post post : posts) {
            post.setEdited(false);
        }
        ThreadModel thread = null;
        try {
            if (threadDao.getThreadById(Long.valueOf(id)) != null) {
                thread = threadDao.getThreadById(Long.valueOf(id));
            }
        } catch (NumberFormatException ignore) {

            if ( threadDao.getThreadBySlug(id) != null) {
                thread = threadDao.getThreadBySlug(id);
            }
        }

        if (thread == null) {
            response.setStatus(404);
            return  new Message("Can't find thread with id or slug  = " + id);
        }

        List<Post> result = new ArrayList<>();

        for (Post post : posts) {
            post.setForum(thread.getForum());
            post.setThread(thread.getId());

            if (!postDao.parentExistOrRootForThread(post.getParent(), thread.getId())) {
                response.setStatus(409);
                return new Message("Can't find parent or parent in another thread " +
                        "(post with id = " + post.getParent() + ") ");
            }
            if (userDao.getByNickname(post.getAuthor()) == null) {
                response.setStatus(404);
                return new Message("Can't find user for post");
            }
            result.add(postDao.createPost(post, currentTime));
        }


        response.setStatus(201);
        return result;
    }

    @GetMapping(value = "/api/thread/{slug_or_id}/details", produces = "application/json")
    public Object getThreadInfo(@PathVariable("slug_or_id") String id,HttpServletResponse response) {
        ThreadModel threadModel;
        try {
            if (threadDao.getThreadById(Long.valueOf(id)) != null) {
                threadModel = threadDao.getThreadById(Long.valueOf(id));
                response.setStatus(200);
                return threadModel;
            }
        } catch (NumberFormatException ignore) {
            if (threadDao.getThreadBySlug(id) != null) {
                threadModel = threadDao.getThreadBySlug(id);
                response.setStatus(200);
                return threadModel;
            } else {
                response.setStatus(404);
                return new Message("Can't find thread with id or slug  = " + id);
            }
        }
        response.setStatus(404);
        return new Message("Can't find thread with id or slug  = " + id);
    }

    @PostMapping(value = "/api/thread/{slug_or_id}/details", produces = "application/json")
    public Object updateThreadInfo(@PathVariable("slug_or_id") String id,  @RequestBody ThreadModel thread, HttpServletResponse response) {
        ThreadModel threadModel;
        try {
            if (threadDao.getThreadById(Long.valueOf(id)) != null) {
                threadModel = threadDao.getThreadById(Long.valueOf(id));
                if (thread.getMessage() != null) {
                    threadModel.setMessage(thread.getMessage());
                }
                if (thread.getTitle() != null) {
                    threadModel.setTitle(thread.getTitle());
                }
                response.setStatus(200);
                threadDao.updateThread(threadModel);
                return threadDao.getThreadById(threadModel.getId());
            }
        } catch (NumberFormatException ignore) {
            if ( threadDao.getThreadBySlug(id) != null) {
                threadModel = threadDao.getThreadBySlug(id);
                if (thread.getMessage() != null) {
                    threadModel.setMessage(thread.getMessage());
                }
                if (thread.getTitle() != null) {
                    threadModel.setTitle(thread.getTitle());
                }
                response.setStatus(200);
                threadDao.updateThread(threadModel);
                return threadDao.getThreadById(threadModel.getId());
            } else {
                response.setStatus(404);
                return  new Message("Can't find thread with id or slug  = " + id);
            }
        }
        response.setStatus(404);
        return  new Message("Can't find thread with id or slug  = " + id);
    }

    @PostMapping(value = "/api/thread/{slug_or_id}/vote", produces = "application/json")
    public Object updateThreadInfo(@PathVariable("slug_or_id") String id, @RequestBody Vote vote, HttpServletResponse response) {
        ThreadModel threadModel = null;

        try {
            if (threadDao.getThreadById(Long.valueOf(id)) != null) {
                threadModel = threadDao.getThreadById(Long.valueOf(id));
                vote.setThreadId(threadModel.getId());

                Vote curVote = voteDao.getByNicknameAndThread(vote.getNickname(), vote.getThreadId());

                if (userDao.getByNickname(vote.getNickname()) == null) {
                    response.setStatus(404);
                    return  new Message("Can't find user for id or slug  = " + id);
                }

                if (curVote == null) {
                    voteDao.createVote(vote);
                } else {
                    curVote.setVoice(vote.getVoice());
                    voteDao.updateVote(curVote);
                }
                threadDao.UpdateVotes(threadModel.getId(), voteDao.sumOfVotes(threadModel.getId()));

                response.setStatus(200);
                return threadDao.getThreadById(threadModel.getId());

            }
        } catch (NumberFormatException ignore) {
            if ( threadDao.getThreadBySlug(id) != null) {
                threadModel = threadDao.getThreadBySlug(id);
                vote.setThreadId(threadModel.getId());

                Vote curVote = voteDao.getByNicknameAndThread(vote.getNickname(), vote.getThreadId());

                if (userDao.getByNickname(vote.getNickname()) == null) {
                    response.setStatus(404);
                    return  new Message("Can't find user for id or slug  = " + id);
                }

                if (curVote == null) {
                    voteDao.createVote(vote);
                } else {
                    curVote.setVoice(vote.getVoice());
                    voteDao.updateVote(curVote);
                }
                threadDao.UpdateVotes(threadModel.getId(), voteDao.sumOfVotes(threadModel.getId()));



                response.setStatus(200);
                return threadDao.getThreadById(threadModel.getId());
            } else {
                response.setStatus(404);
                return  new Message("Can't find thread with id or slug  = " + id);
            }
        }
        response.setStatus(404);
        return  new Message("Can't find thread with id or slug  = " + id);
    }

    @GetMapping(value = "/api/post/{id}/details", produces = "application/json")
    public Object getPostInf(@PathVariable("id") Long id, @RequestParam(value = "related", required = false)
            String[] params , HttpServletResponse response ) {
        boolean user = false;
        boolean forum = false;
        boolean thread = false;
        try {
            for (String par : params) {
                switch (par) {
                    case "user":
                        user = true;
                        break;
                    case "forum":
                        forum = true;
                        break;
                    case "thread":
                        thread = true;
                        break;
                }
            }
        } catch (NullPointerException ignored) {
        }
        HashMap<String,Object> hashMap = new HashMap<>();
        Post curPost = postDao.getPostById(id);
        if (curPost == null) {
            response.setStatus(404);
            return  new Message("Can't find post with id = " + id);
        }
        if (user) {
            hashMap.put("author", userDao.getByNickname(curPost.getAuthor()));
        }
        if (forum) {
            String slug = curPost.getForum();
            forumDao.updateForumInfo(slug, threadDao.countOfThreads(slug), postDao.countOfPosts(slug));
            hashMap.put("forum", forumDao.getForumForSlug(curPost.getForum()));
        }
        hashMap.put("post", curPost);
        if (thread) {
            hashMap.put("thread", threadDao.getThreadById(curPost.getThread()));
        }
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
        if (post.getMessage() != null && !post.getMessage().equals("")) {
            if (!post.getMessage().equals(curPost.getMessage())) {
                postDao.setIsEdited(curPost.getId());
            }
            curPost.setMessage(post.getMessage());
            postDao.updatePost(curPost);
        }
        response.setStatus(200);
        return postDao.getPostById(id);
    }

}
