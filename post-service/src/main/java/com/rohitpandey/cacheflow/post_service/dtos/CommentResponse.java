package com.rohitpandey.cacheflow.post_service.dtos;

import lombok.Builder;

@Builder
public record CommentResponse(
        Long id,
        Long postId,
        String name,
        String email,
        String body
) {}
