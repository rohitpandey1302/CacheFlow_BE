package com.rohitpandey.cacheflow.sync_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohitpandey.cacheflow.sync_service.service.CommentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncEventConsumer {
    private final CommentSyncService commentSyncService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "sync-events",
            groupId = "sync-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onSyncEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Received Kafka event: key={} partition={} offset={}",
                    record.key(), record.partition(), record.offset());

            JsonNode event = objectMapper.readTree(record.value());
            String eventType = event.get("eventType").asText();

            if ("POSTS_SYNCED".equals(eventType)) {
                int count = event.get("count").asInt();
                log.info("POSTS_SYNCED event received — {} new posts. Triggering comment sync.", count);
                commentSyncService.syncCommentsForAllPosts();
            } else {
                log.debug("Ignoring unknown event type: {}", eventType);
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process sync event — will NOT acknowledge (Kafka will retry)", e);
        }
    }
}
