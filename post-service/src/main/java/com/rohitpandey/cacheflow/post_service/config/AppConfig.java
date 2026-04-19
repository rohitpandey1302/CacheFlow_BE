package com.rohitpandey.cacheflow.post_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    //-------Kafka Topics--------

    @Bean
    public NewTopic syncEventTopic() {
        return TopicBuilder.name("sync-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //-------HTTP Client--------

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "CacheFlowApp/1.0")
                .build();
    }
}
