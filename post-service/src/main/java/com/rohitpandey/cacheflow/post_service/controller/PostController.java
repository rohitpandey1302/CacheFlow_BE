package com.rohitpandey.cacheflow.post_service.controller;

import com.rohitpandey.cacheflow.post_service.dtos.PostResponse;
import com.rohitpandey.cacheflow.post_service.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
