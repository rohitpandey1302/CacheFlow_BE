package com.rohitpandey.cacheflow.post_service.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PostResponse(
        Long id,
        Long userId,
        String title,
        String body,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<CommentResponse> comments
) {}
