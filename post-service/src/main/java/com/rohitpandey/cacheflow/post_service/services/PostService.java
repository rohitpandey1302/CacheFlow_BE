package com.rohitpandey.cacheflow.post_service.services;

import com.rohitpandey.cacheflow.post_service.client.JsonPlaceholderClient;
import com.rohitpandey.cacheflow.post_service.client.JsonPlaceholderDtos;
import com.rohitpandey.cacheflow.post_service.dtos.CommentResponse;
import com.rohitpandey.cacheflow.post_service.dtos.PagedResponse;
import com.rohitpandey.cacheflow.post_service.dtos.PostResponse;
import com.rohitpandey.cacheflow.post_service.dtos.SyncEvent;
import com.rohitpandey.cacheflow.post_service.exception.PostNotFoundException;
import com.rohitpandey.cacheflow.post_service.kafka.SyncEventProducer;
import com.rohitpandey.cacheflow.post_service.mappers.Mappers;
import com.rohitpandey.cacheflow.post_service.models.Comment;
import com.rohitpandey.cacheflow.post_service.models.Post;
import com.rohitpandey.cacheflow.post_service.repositories.CommentRepository;
import com.rohitpandey.cacheflow.post_service.repositories.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JsonPlaceholderClient externalClient;
    private final SyncEventProducer syncEventProducer;

    private final Mappers mappers;

    public static final int PAGE_SIZE = 10;

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

    @Transactional
    public int syncPostsFromExternal() {
        log.info("Starting post sync from JSONPlaceholder");

        List<JsonPlaceholderDtos.ExternalPost> externalPosts = externalClient.fetchAllPosts();
        if (externalPosts.isEmpty()) {
            log.warn("No posts received from JSONPlaceholder");
            return 0;
        }

        Set<Long> existingIds = Set.copyOf(postRepository.findAllIds());

        List<Post> toInsert = externalPosts.stream()
                .filter(ep -> !existingIds.contains(ep.getId()))
                .map(ep -> Post.builder()
                        .id(ep.getId())
                        .userId(ep.getUserId())
                        .title(ep.getTitle())
                        .body(ep.getBody())
                        .build()
                ).toList();

        if (!toInsert.isEmpty()) {
            postRepository.saveAll(toInsert);
            log.info("Synced {} new posts into MySQL", toInsert.size());
        }

        syncEventProducer.publishSyncEvent(SyncEvent.builder()
                .eventType("POSTS_SYNCED")
                .count(toInsert.size())
                .syncedAt(Instant.now())
                .build());

        return toInsert.size();
    }

    @Transactional
    public int syncCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            log.warn("Skipping comment sync — postId={} not found locally", postId);
            return 0;
        }

        List<JsonPlaceholderDtos.ExternalComment> externalComments = externalClient.fetchAllComments(postId);
        Set<Long> existingIds = new HashSet<>(commentRepository.findAllIds());

        List<Comment> toInsert = externalComments.stream()
                .filter(ec -> !existingIds.contains(ec.getId()))
                .map(ec -> Comment.builder()
                        .id(ec.getId())
                        .post(post)
                        .name(ec.getName())
                        .email(ec.getEmail())
                        .body(ec.getBody())
                        .build()).toList();

        if (!toInsert.isEmpty()) {
            commentRepository.saveAll(toInsert);
            log.info("Synced {} new comments into MySQL", toInsert.size());
        }

        return toInsert.size();
    }

    public PagedResponse<PostResponse> getPosts(int page) {
        var pageable = PageRequest.of(page-1, PAGE_SIZE);
        var result = postRepository.findAllByOrderByIdAsc(pageable);

        List<PostResponse> postResponses = result.getContent().stream()
                .map(mappers::toPostResponse)
                .toList();

        return PagedResponse.of(postResponses, page, PAGE_SIZE, result.getTotalElements());
    }
}
