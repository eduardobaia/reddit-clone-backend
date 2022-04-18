package com.reddit.redditclone.controller;

import com.reddit.redditclone.dto.CommentsDto;
import com.reddit.redditclone.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentsController {
    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<Object> createComment(@RequestBody CommentsDto commentsDto){
        commentService.save(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/by-post/{postId}")
    private ResponseEntity<List<CommentsDto>>  getAllCommentsForPost(@PathVariable Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(postId));
    }

    @GetMapping("/by-user/{userName}")
    private ResponseEntity<List<CommentsDto>> gfetAllCommentsForUser(@PathVariable String userName){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(userName));
    }
}
