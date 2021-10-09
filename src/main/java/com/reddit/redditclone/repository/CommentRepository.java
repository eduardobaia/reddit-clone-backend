package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Comment;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
