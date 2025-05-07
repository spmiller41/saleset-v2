package com.saleset.core.service.transaction;

import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.EventDataTransfer;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.EventSource;
import com.saleset.core.service.cache.EventCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventTransactionManager {

    private final Logger logger = LoggerFactory.getLogger(EventTransactionManager.class);

    private final EventRepo eventRepo;
    private final EventCacheManager cacheManager;
    private final LeadRepo leadRepo;

    @Autowired
    public EventTransactionManager(EventRepo eventRepo, EventCacheManager cacheManager, LeadRepo leadRepo) {
        this.eventRepo = eventRepo;
        this.cacheManager = cacheManager;
        this.leadRepo = leadRepo;
    }

    /**
     * Inserts an event into the system if it is deemed insertable based on cache validation.
     * <p>
     * This method first checks if the event data is insertable using the cache manager. If not,
     * it returns an empty {@code Optional}. If the event is insertable, it attempts to retrieve
     * the associated lead using its UUID. If the lead exists, a new event is created and inserted
     * into the repository. If insertion succeeds, the inserted event is returned; otherwise,
     * an error is logged and an empty {@code Optional} is returned.
     * </p>
     *
     * @param eventData The event data transfer object containing event details.
     * @return An {@code Optional} containing the inserted event if successful, otherwise empty.
     */
    public Optional<Event> insertEventHandler(EventDataTransfer eventData, EventSource eventSource) {
        boolean isInsertable = cacheManager.cacheEvent(eventData);
        if (!isInsertable) return Optional.empty();

        Optional<Lead> optLead = leadRepo.findLeadByUUID(eventData.getLeadUUID());
        if (optLead.isPresent()) {
            Event event = new Event(optLead.get(), eventData, eventSource);
            Optional<Event> optEvent = eventRepo.safeInsert(event);
            if (optEvent.isPresent()) {
                logger.info("Event inserted successfully: {}", optEvent.get());
                return optEvent;
            }
        } else {
            logger.error("Lead could not be located while searching by uuid via event. Event data: {}", eventData);
        }

        return Optional.empty();
    }

}
