package com.saleset.core.rest;

import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.SGEventDataTransfer;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
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
    private LeadRepo leadRepo;

    @Autowired
    private EventRepo eventRepo;

    @PostMapping
    public void emailEvent(@RequestBody List<SGEventDataTransfer> eventDataList) {
        eventDataList.forEach(eventData -> {
            System.out.println(eventData);

            Optional<Lead> optLead = leadRepo.findLeadByUUID(eventData.getLeadUUID());
            optLead.ifPresent(lead -> {
                Event event = new Event(lead, eventData);
                Optional<Event> optEvent = eventRepo.safeInsert(event);
                optEvent.ifPresent(newEvent -> { System.out.println("New Event Created: " + newEvent); });
            });
        });
    }

}
