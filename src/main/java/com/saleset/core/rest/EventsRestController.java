package com.saleset.core.rest;

import com.saleset.core.dto.EventDataTransfer;
import com.saleset.core.entities.Event;
import com.saleset.core.service.transaction.EventTransactionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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

    // Anonymous endpoint name for click tracking - to be masked in booking link
    @GetMapping("/go")
    public void smsEvent(@RequestParam("UUID") String leadUUID) {
        EventDataTransfer eventData = new EventDataTransfer();
        eventData.setEvent("click");
        eventData.setLeadUUID(leadUUID);
        System.out.println(eventData);
    }

}
