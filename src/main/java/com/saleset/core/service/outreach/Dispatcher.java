package com.saleset.core.service.outreach;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.MarketZipDataRepo;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.integration.sendgrid.SendGridManager;
import com.saleset.integration.twilio.service.TwilioManager;
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
    private final AppointmentRepo appointmentRepo;
    private final SendGridManager sendGridManager;

    @Autowired
    public Dispatcher(TwilioManager twilioManager,
                      ContactRepo contactRepo,
                      AddressRepo addressRepo,
                      MarketZipDataRepo mzdRepo,
                      AppointmentRepo appointmentRepo,
                      SendGridManager sendGridManager) {
        this.twilioManager = twilioManager;
        this.contactRepo = contactRepo;
        this.addressRepo = addressRepo;
        this.mzdRepo = mzdRepo;
        this.appointmentRepo = appointmentRepo;
        this.sendGridManager = sendGridManager;
    }


    public void executeSmsFollowUp(Lead lead) {
        Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
        if (optContact.isEmpty()) {
            logger.error("Contact could not be found when attempting dispatch for Lead: {}", lead);
            return;
        }

        String fromNumber = determineFromNumber(lead, optContact.get());
        int followUpCount = lead.getFollowUpCount() + 1;
        String body = "Hello, " + optContact.get().getFirstName()
                + ". Current beta testing. Booking link below: \n" + lead.getTrackingWebhookUrl()
                + "\n" + "Follow-up count: " + followUpCount;

        twilioManager.sendSMS(fromNumber, optContact.get().getPrimaryPhone(), body);
    }


    public void executeFollowUpEmail(Lead lead) {
        Address address;
        Optional<Address> optAddress = addressRepo.findAddressByLead(lead);
        address = optAddress.orElse(new Address());

        Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
        if (optContact.isEmpty()) return;

        Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);
        if (optAppointment.isEmpty()) return;

        sendGridManager.sendFollowUpCallAlert(lead, optContact.get(), address);
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
