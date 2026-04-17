package com.rohitpandey.cacheflow.post_service.controller;

import com.rohitpandey.cacheflow.post_service.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentsController {
    private final PostService postService;

    @PostMapping("/{postId}/sync-comments")
    public ResponseEntity<String> syncCommentsForPost(@PathVariable Long postId) {
        int count = postService.syncCommentsForPost(postId);
        return ResponseEntity.ok("Synced " + count + " new comments for post " + postId);
    }
}
