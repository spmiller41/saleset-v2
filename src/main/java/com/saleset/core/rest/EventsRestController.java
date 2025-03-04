package com.saleset.core.rest;

import com.saleset.core.dto.EventDataTransfer;
import com.saleset.core.entities.Event;
import com.saleset.core.service.transaction.EventTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v2/api")
public class EventsRestController {

    @Autowired
    private EventTransactionManager eventTransactionManager;

    @PostMapping("/email_event")
    public void emailEvent(@RequestBody List<EventDataTransfer> eventDataList) {
        eventDataList.forEach(eventData -> {
            Optional<Event> optEvent = eventTransactionManager.insertEventHandler(eventData);
        });
    }

}
