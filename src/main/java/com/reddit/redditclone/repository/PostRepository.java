package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.Subreddit;
import com.reddit.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);
    List<Post> findByUser(User user);
}
