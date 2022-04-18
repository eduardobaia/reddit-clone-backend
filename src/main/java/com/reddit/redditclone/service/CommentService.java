package com.reddit.redditclone.service;

import com.reddit.redditclone.dto.CommentsDto;
import com.reddit.redditclone.exceptions.PostNotFoundException;
import com.reddit.redditclone.mapper.CommentMapper;
import com.reddit.redditclone.model.Comment;
import com.reddit.redditclone.model.NotificationEmail;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.User;
import com.reddit.redditclone.repository.CommentRepository;
import com.reddit.redditclone.repository.PostRepository;
import com.reddit.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL = "";
    private  final PostRepository postRepository;
    private  final UserRepository userRepository;
    private final AuthService authService;
    private  final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;


    public void save(CommentsDto commentsDto){
        Post post =postRepository.findById(commentsDto.getPostId()).orElseThrow(
                () -> new PostNotFoundException(commentsDto.getPostId().toString())
        );
        Comment comment = commentMapper.map(commentsDto,post,authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + "posted a comment in your post. "+ POST_URL);
        sendCommentNotification(message, post.getUser());

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + "Commentd onyour post", user.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException(postId.toString()));

        return  commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());

    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(()-> new UsernameNotFoundException(userName));

        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());

    }
}
