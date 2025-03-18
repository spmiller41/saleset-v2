package com.saleset.core.rest;

import com.saleset.core.dto.EventDataTransfer;
import com.saleset.core.entities.Event;
import com.saleset.core.service.transaction.EventTransactionManager;
import com.saleset.core.util.QueryUrlGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v2/api")
public class EventsRestController {

    @Autowired
    private EventTransactionManager eventTransactionManager;

    @Autowired
    private QueryUrlGenerator queryUrlGenerator;

    @PostMapping("/email_event")
    public void emailEvent(@RequestBody List<EventDataTransfer> eventDataList) {
        eventDataList.forEach(eventData -> {
            Optional<Event> optEvent = eventTransactionManager.insertEventHandler(eventData);
        });
    }

    // Anonymous endpoint name for click tracking - to be masked in booking link
    @GetMapping("/go")
    public ResponseEntity<Void> smsEvent(@RequestParam("UUID") String leadUUID,
                         @RequestParam("FNAME") String firstName,
                         @RequestParam("LNAME") String lastName,
                         @RequestParam("EMAIL") String email,
                         @RequestParam("PHONE_NUMBER") String phone) {
        EventDataTransfer eventData = new EventDataTransfer();
        eventData.setEvent("click");
        eventData.setLeadUUID(leadUUID);
        System.out.println(eventData);
        System.out.printf("First Name: %s%nLast Name: %s%nEmail: %s%nPhone: %s%n",
                firstName, lastName, email, phone);

        String bookingUrl = queryUrlGenerator.buildBooking(leadUUID, firstName, lastName, email, phone);
        System.out.println(bookingUrl);

        Optional<Event> optEvent = eventTransactionManager.insertEventHandler(eventData);

        // Return HTTP 302 redirect to the booking URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, bookingUrl)
                .build();
    }

}
