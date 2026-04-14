package com.rohitpandey.cacheflow.post_service.mappers;

import com.rohitpandey.cacheflow.post_service.dtos.CommentResponse;
import com.rohitpandey.cacheflow.post_service.dtos.PostResponse;
import com.rohitpandey.cacheflow.post_service.models.Comment;
import com.rohitpandey.cacheflow.post_service.models.Post;
import org.springframework.stereotype.Component;

@Component
public class Mappers {
    public PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .body(post.getBody())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public CommentResponse toCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .name(comment.getName())
                .email(comment.getEmail())
                .body(comment.getBody())
                .build();
    }
}
