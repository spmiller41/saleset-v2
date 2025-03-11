package com.saleset.core.util;

import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class BookingUrlGenerator {

    @Value("${event.tracking.webhook}")
    private String eventTrackingWebhook;

    public String build(Lead lead, Contact contact) {
        return String.format("%s?FNAME=%s&LNAME=%s&EMAIL=%s&PHONE_NUMBER=%s&UUID=%s",
                eventTrackingWebhook,
                encode(contact.getFirstName()),
                encode(contact.getLastName()),
                encode(contact.getEmail()),
                encode(contact.getPrimaryPhone()),
                encode(lead.getUuid()));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
