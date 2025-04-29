package com.saleset.core.util;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dao.MarketZipDataRepo;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.entities.MarketZipData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class QueryUrlGenerator {

    @Value("${event.tracking.webhook}")
    private String eventTrackingWebhook;

    @Value("${booking.url}")
    private String bookingUrl;

    @Value("${booking.virtual.url}")
    private String bookingVirtualUrl;

    private final MarketZipDataRepo mzdRepo;

    public QueryUrlGenerator(MarketZipDataRepo mzdRepo) { this.mzdRepo = mzdRepo; }

    public String buildTracking(Lead lead) {
        return String.format("%s?UUID=%s", eventTrackingWebhook, encode(lead.getUuid()));
    }

    @Transactional
    public String buildBooking(Lead lead, Contact contact, Address address) {
        return String.format("%s?FNAME=%s&LNAME=%s&EMAIL=%s&PHONE_NUMBER=%s&UUID=%s",
                determineBookingPage(address),
                encode(contact.getFirstName()),
                encode(contact.getLastName()),
                encode(contact.getEmail()),
                encode(contact.getPrimaryPhone()),
                encode(lead.getUuid()));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String determineBookingPage(Address address) {
        if (address == null || address.getZipCode() == null) {
            return bookingVirtualUrl;
        }

        return mzdRepo.findByAddress(address)
                .filter(mzd -> "ANY".equals(mzd.getAppointmentType()))
                .map(mzd -> bookingUrl)
                .orElse(bookingVirtualUrl);
    }

}