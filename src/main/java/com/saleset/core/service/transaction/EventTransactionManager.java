package com.saleset.core.service.transaction;

import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.service.cache.EventCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventTransactionManager {

    private final Logger logger = LoggerFactory.getLogger(EventTransactionManager.class);

    private EventRepo eventRepo;
    private EventCacheManager cacheManager;
    private LeadRepo leadRepo;

    @Autowired
    public EventTransactionManager(EventRepo eventRepo, EventCacheManager cacheManager, LeadRepo leadRepo) {
        this.eventRepo = eventRepo;
        this.cacheManager = cacheManager;
        this.leadRepo = leadRepo;
    }



}
