package com.rohitpandey.cacheflow.sync_service.service;

import com.rohitpandey.cacheflow.sync_service.client.PostServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentSyncService {
    private final PostServiceClient postServiceClient;

    public void syncCommentsForAllPosts() {
        log.info("Starting comment sync for all posts");

        int totalPages = postServiceClient.fetchTotalPages();
        if (totalPages == 0) {
            log.warn("No posts found in post-service — skipping comment sync");
            return;
        }

        List<Long> allPostIds = new ArrayList<>();
        for (int page=1; page <= totalPages; page++) {
            allPostIds.addAll(postServiceClient.fetchPostIds(page));
        }

        log.info("Found {} posts to sync comments for", allPostIds.size());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount   = new AtomicInteger(0);

        for (Long postId: allPostIds) {
            try {
                postServiceClient.triggerCommentSyncForPost(postId);
                successCount.incrementAndGet();
            } catch (Exception e) {
                log.warn("Comment sync failed for postId={}: {}", postId, e.getMessage());
                failCount.incrementAndGet();
            }
        }

        log.info("Comment sync complete — success={} failed={}", successCount.get(), failCount.get());
    }
}
