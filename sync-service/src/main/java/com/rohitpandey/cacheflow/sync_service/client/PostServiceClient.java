package com.rohitpandey.cacheflow.sync_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceClient {
    private final RestClient restClient;

    @Value("${services.post-service.url:http://localhost:8081}")
    private String postServiceUrl;

    public void triggerPostSync() {
        try {
            String result = restClient.post()
                    .uri(postServiceUrl + "/api/posts/sync")
                    .retrieve()
                    .body(String.class);

            log.info("Post sync triggered: {}", result);
        } catch (Exception e) {
            log.error("Failed to trigger post sync on post-service", e);
            throw new RuntimeException("Post sync failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Long> fetchPostIds(int page) {
        try {
            Map<String, Object> response= restClient.get()
                    .uri(postServiceUrl + "/api/posts?page=" + page)
                    .retrieve()
                    .body(Map.class);

            if (response == null || !response.containsKey("data")) {
                return List.of();
            }
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
            return data.stream()
                    .map(post -> ((Number) post.get("id")).longValue())
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch post IDs from post-service page={}", page, e);
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    public int fetchTotalPages() {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(postServiceUrl + "/api/posts?page=1")
                    .retrieve()
                    .body(Map.class);

            if (response == null) return 0;
            return ((Number) response.get("totalPages")).intValue();
        } catch (Exception e) {
            log.error("Failed to fetch total pages from post-service", e);
            return 0;
        }
    }

    public void triggerCommentSyncForPost(Long postId) {
        try {
            restClient.post()
                    .uri(postServiceUrl + "/api/posts/" + postId + "/sync-comments")
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to sync comments for postId={}: {}", postId, e.getMessage());
        }
    }
}
