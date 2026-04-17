package com.rohitpandey.cacheflow.post_service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class JsonPlaceholderDtos {

    @Data
    public static class ExternalPost {
        private Long id;
        @JsonProperty("userId")
        private Long userId;
        private String title;
        private String body;
    }

    @Data
    public static class ExternalComment {
        private Long id;
        @JsonProperty("postId")
        private Long postId;
        private String name;
        private String email;
        private String body;
    }
}
