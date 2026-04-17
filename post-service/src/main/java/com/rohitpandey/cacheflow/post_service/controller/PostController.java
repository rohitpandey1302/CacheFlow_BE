package com.rohitpandey.cacheflow.post_service.controller;

import com.rohitpandey.cacheflow.post_service.dtos.CommentResponse;
import com.rohitpandey.cacheflow.post_service.dtos.PagedResponse;
import com.rohitpandey.cacheflow.post_service.dtos.PostResponse;
import com.rohitpandey.cacheflow.post_service.services.PostService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> triggerSync() {
        int count = postService.syncPostsFromExternal();
        return ResponseEntity.ok("Synced " + count + " new posts");
    }

    @GetMapping
    public ResponseEntity<PagedResponse<PostResponse>> getPosts(
            @RequestParam(defaultValue = "1") @Min(1) int page
    ) {
        return ResponseEntity.ok(postService.getPosts(page));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<PagedResponse<CommentResponse>> getComments(
            @PathVariable Long id
    ) {
        var post = postService.getPostById(id);
        return ResponseEntity.ok(
                PagedResponse.of(
                        post.comments(),
                    1,
                        post.comments().size(),
                        post.comments().size()
                )
        );
    }
}
