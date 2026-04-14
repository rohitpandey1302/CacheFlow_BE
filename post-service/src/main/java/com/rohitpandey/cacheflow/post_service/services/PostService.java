package com.rohitpandey.cacheflow.post_service.services;

import com.rohitpandey.cacheflow.post_service.dtos.CommentResponse;
import com.rohitpandey.cacheflow.post_service.dtos.PostResponse;
import com.rohitpandey.cacheflow.post_service.exception.PostNotFoundException;
import com.rohitpandey.cacheflow.post_service.mappers.Mappers;
import com.rohitpandey.cacheflow.post_service.models.Post;
import com.rohitpandey.cacheflow.post_service.repositories.CommentRepository;
import com.rohitpandey.cacheflow.post_service.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final Mappers mappers;

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        List<CommentResponse> commentResponses = commentRepository.findByPostId(id).stream()
                .map(mappers::toCommentResponse)
                .toList();

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .body(post.getBody())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(commentResponses)
                .build();
    }
}
