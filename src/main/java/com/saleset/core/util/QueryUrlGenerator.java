package com.saleset.core.util;

import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class QueryUrlGenerator {

    @Value("${event.tracking.webhook}")
    private String eventTrackingWebhook;

    @Value("${booking.url}")
    private String bookingUrl;

    public String buildTracking(Lead lead, Contact contact) {
        return String.format("%s?FNAME=%s&LNAME=%s&EMAIL=%s&PHONE_NUMBER=%s&UUID=%s",
                eventTrackingWebhook,
                encode(contact.getFirstName()),
                encode(contact.getLastName()),
                encode(contact.getEmail()),
                encode(contact.getPrimaryPhone()),
                encode(lead.getUuid()));
    }

    public String buildBooking(String leadUUID, String firstName, String lastName, String email, String phone) {
        return String.format("%s?FNAME=%s&LNAME=%s&EMAIL=%s&PHONE_NUMBER=%s&UUID=%s",
                bookingUrl,
                encode(firstName),
                encode(lastName),
                encode(email),
                encode(phone),
                encode(leadUUID));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}