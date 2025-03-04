package com.saleset.core.rest;

import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.SGEventDataTransfer;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import com.saleset.core.service.cache.EventCacheManager;
import com.saleset.core.service.transaction.EventTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("v2/api/email_events")
public class EmailEventsRestController {

    @Autowired
    private EventTransactionManager eventTransactionManager;

    @PostMapping
    public void emailEvent(@RequestBody List<SGEventDataTransfer> eventDataList) {
        eventDataList.forEach(eventData -> {
            Optional<Event> optEvent = eventTransactionManager.insertEventHandler(eventData);
        });
    }

}
