package com.rohitpandey.cacheflow.post_service.dtos;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SyncEvent(
        String eventType,   // "POSTS_SYNCED" | "COMMENTS_SYNCED"
        int count,
        Instant syncedAt
) {}
