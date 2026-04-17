package com.rohitpandey.cacheflow.post_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonPlaceholderClient {
    private final RestClient restClient;
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public List<JsonPlaceholderDtos.ExternalPost> fetchAllPosts() {
        try {
            var posts = restClient.get().uri(BASE_URL+"/posts")
                    .retrieve()
                    .body(JsonPlaceholderDtos.ExternalPost[].class);

            return posts != null ? Arrays.asList(posts) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch posts from JSONPlaceholder", e);
            return Collections.emptyList();
        }
    }

    public List<JsonPlaceholderDtos.ExternalComment> fetchAllComments(Long postId) {
        try {
            var comments = restClient.get().uri(BASE_URL+"/posts/{postId}/comments", postId)
                    .retrieve()
                    .body(JsonPlaceholderDtos.ExternalComment[].class);

            return comments != null ? Arrays.asList(comments) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch comments for postId={} from JSONPlaceholder", postId, e);
            return Collections.emptyList();
        }
    }
}
