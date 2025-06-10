package com.saleset.core.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.saleset.core.dto.EventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class EventCacheManager {

    private final Logger logger = LoggerFactory.getLogger(EventCacheManager.class);

    private final Cache<String, Set<String>> eventCache;

    @Autowired
    public EventCacheManager(Cache<String, Set<String>> eventCache) {
        this.eventCache = eventCache;
    }

    /**
     * Caches an event for a given lead to prevent duplicate event storage.
     *
     * <p>This method ensures that an event (e.g., "open", "click") is only persisted if
     * it has not already been cached for the given lead within the cache's time window.
     *
     * <p>If the event is already cached, it returns {@code false} to indicate that
     * persistence should be skipped. If the event is new, it is added to the cache
     * and the method returns {@code true} to indicate that it should be persisted.
     *
     * @param eventData The event data containing the type of event (e.g., "open", "click") and the lead uuid.
     * @return {@code true} if the event was newly cached and should be persisted,
     *         {@code false} if the event was already cached and should not be persisted.
     */
    public boolean cacheEvent(EventRequest eventData) {
        String leadUUID = eventData.getLeadUUID();
        String eventType = eventData.getEvent();

        Set<String> cachedEvents = eventCache.getIfPresent(leadUUID);

        if (cachedEvents == null) {
            cachedEvents = new HashSet<>(); // If no events are cached for this UUID, create a new mutable set.
        } else if (cachedEvents.contains(eventType)) {
            return false; // Return false to indicate the event should NOT be persisted.
        }

        // Add the new event type.
        cachedEvents.add(eventType);

        // Update the cache with the modified set.
        eventCache.put(leadUUID, cachedEvents);

        // Verify caching success
        if (cachedEvents.contains(eventData.getEvent())) {
            logger.info("Successfully cached event: {} for Lead UUID: {}", eventType, leadUUID);
        } else {
            logger.error("Failed to cache event: {} for Lead UUID: {}", eventType, leadUUID);
        }

        return true;
    }

}
