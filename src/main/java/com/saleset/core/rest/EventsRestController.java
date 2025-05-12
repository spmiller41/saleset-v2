package com.saleset.core.rest;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.EventDataTransfer;
import com.saleset.core.entities.Event;
import com.saleset.core.enums.EventSource;
import com.saleset.core.service.transaction.leads.LeadEngagementManager;
import com.saleset.core.service.transaction.EventTransactionManager;
import com.saleset.core.util.QueryUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v2/api")
public class EventsRestController {

    @Autowired
    private EventTransactionManager eventTransactionManager;

    @Autowired
    private QueryUrlGenerator queryUrlGenerator;

    @Autowired
    private LeadEngagementManager leadEngagementManager;

    @Autowired
    private LeadRepo leadRepo;

    @PostMapping("/email_event")
    public void emailEvent(@RequestBody List<EventDataTransfer> eventDataList) {
        eventDataList.forEach(eventData -> {
            Optional<Event> optEvent = eventTransactionManager.insertEventHandler(eventData, EventSource.EMAIL);
        });
    }

    // Anonymous endpoint name for click tracking - to be masked in booking link
    @GetMapping("/go")
    public ResponseEntity<Void> smsEvent(@RequestParam("UUID") String leadUUID) {
        EventDataTransfer eventData = new EventDataTransfer();
        eventData.setEvent("click");
        eventData.setLeadUUID(leadUUID);
        System.out.println(eventData);

        String bookingUrl = leadEngagementManager.getBookingPageUrl(leadUUID);
        System.out.println(bookingUrl);

        Optional<Event> optEvent = eventTransactionManager.insertEventHandler(eventData, EventSource.SMS);
        optEvent.flatMap(event -> leadRepo.findLeadById(event.getLeadId())).ifPresent(lead -> {
            if (lead.getAddressId() != null) {
                leadEngagementManager.updateEngagementOnLeadEvent(lead);
            }
        });

        // Return HTTP 302 redirect to the booking URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, bookingUrl)
                .build();
    }


}
