package com.rohitpandey.cacheflow.sync_service.scheduler;

import com.rohitpandey.cacheflow.sync_service.client.PostServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncScheduler {
    private final PostServiceClient postServiceClient;

    @Scheduled(
            initialDelayString = "${sync.schedule.initial-delay-ms:60000}",
            fixedDelayString = "${sync.schedule.fixed-delay-ms:1800000}"
    )
    public void scheduledSync() {
        log.info("=== Scheduled sync triggered ===");
        try {
            postServiceClient.triggerPostSync();
            log.info("=== Scheduled sync dispatched successfully ===");
        } catch (Exception e) {
            log.error("=== Scheduled sync failed — will retry next cycle ===", e);
        }
    }
}
