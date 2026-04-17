package com.rohitpandey.cacheflow.post_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohitpandey.cacheflow.post_service.dtos.SyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncEventProducer {
    public static final String SYNC_EVENTS_TOPIC = "sync-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishSyncEvent(SyncEvent syncEvent) {
        try {
            String payload = objectMapper.writeValueAsString(syncEvent);
            kafkaTemplate.send(SYNC_EVENTS_TOPIC, syncEvent.eventType(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish SyncEvent: {}", syncEvent.eventType(), ex);
                        } else {
                            log.info("Published SyncEvent: {} → partition={} offset={}",
                                    syncEvent.eventType(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize SyncEvent", ex);
        }
    }
}
