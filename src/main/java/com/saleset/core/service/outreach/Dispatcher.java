package com.saleset.core.service.outreach;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.MarketZipDataRepo;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.service.sms.TwilioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Dispatcher implements PhoneRoutingStrategy {

    @Value("${twilio.lips.number}")
    private String lipsNumber;

    @Value("${twilio.nyps.number}")
    private String nypsNumber;

    private final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private final TwilioManager twilioManager;
    private final ContactRepo contactRepo;
    private final AddressRepo addressRepo;
    private final MarketZipDataRepo mzdRepo;

    @Autowired
    public Dispatcher(TwilioManager twilioManager, ContactRepo contactRepo, AddressRepo addressRepo, MarketZipDataRepo mzdRepo) {
        this.twilioManager = twilioManager;
        this.contactRepo = contactRepo;
        this.addressRepo = addressRepo;
        this.mzdRepo = mzdRepo;
    }


    public void executeFollowUp(Lead lead) {
        Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
        if (optContact.isEmpty()) {
            logger.error("Contact could not be found when attempting dispatch for Lead: {}", lead);
            return;
        }

        String fromNumber = determineFromNumber(lead, optContact.get());
        System.out.println("From Number: " + fromNumber);
    }


    @Override
    public String getLipsNumber() { return lipsNumber; }

    @Override
    public String getNypsNumber() { return nypsNumber; }

    @Override
    public MarketZipDataRepo getMarketZipDataRepo() { return mzdRepo; }

    @Override
    public AddressRepo getAddressRepo() { return addressRepo; }

}
