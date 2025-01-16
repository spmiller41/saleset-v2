package com.saleset.core.service.transaction;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.engine.EngagementEngineImpl;
import com.saleset.core.util.Validation;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class LeadTransactionManagerImpl implements LeadTransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(LeadTransactionManagerImpl.class);

    private final EngagementEngineImpl engagementEngine;
    private final ContactRepo contactRepo;
    private final AddressRepo addressRepo;
    private final LeadRepo leadRepo;
    private final EventRepo eventRepo;

    @Autowired
    public LeadTransactionManagerImpl(EngagementEngineImpl engagementEngine, ContactRepo contactRepo,
                                      AddressRepo addressRepo, LeadRepo leadRepo, EventRepo eventRepo) {
        this.engagementEngine = engagementEngine;
        this.contactRepo = contactRepo;
        this.addressRepo = addressRepo;
        this.leadRepo = leadRepo;
        this.eventRepo = eventRepo;
    }

    @Override
    @Transactional
    public void manageLead(LeadDataTransfer leadData) {

        // If the client only supplied a secondary number, swap it to primary.
        if (leadData.getPrimaryPhone() == null && leadData.getSecondaryPhone() != null) {
            leadData.setPrimaryPhone(leadData.getSecondaryPhone());
            leadData.setSecondaryPhone(null);
        }

        Optional<Contact> optContact = contactRepo.findContactByPhone(leadData);
        optContact.ifPresent(contact -> {
            List<Lead> leadList = leadRepo.findLeadByContact(contact);

            // If the Contact exists and the Lead doesn't, we have a problem.
            if (leadList.isEmpty()) {
                logger.error("Contact exists without lead: {}", contact);
                return;
            }

            // If the Contact belongs to Lead on DNC, kick Lead. Make sure to traverse entire list.
            leadList.forEach(lead -> {
                if (lead.getStage().equalsIgnoreCase(LeadStage.DNC.toString())) {
                    logger.warn("Contact belongs to Lead on DNC. Kicking Lead.");
                    return;
                }

                // If this Lead contains an Address, check whether it matches the Address payload (leadData)
                if (lead.getAddressId() != null) {
                    Optional<Address> optAddress = addressRepo.findAddressByLead(lead);

                    /*
                     * Note: If this Leads' Contact and Address match the payload (leadData), process the reentry.
                     * In contrast, if there's no the Address doesn't match (new Address), generate a new lead.
                     */
                    optAddress.ifPresentOrElse(address ->
                            processLeadResumption(leadData, address, lead), () ->
                            processNewAddressExistingContact(leadData, optContact.get(), lead));
                } // Enter logic here to process a lead reentering. Note, An existing contact with no address match fits this scenario also.
            });
        });
    }




    /*
     * Handles leads that could possibly reenter the system.
     * If Address is found via ID and matches payload (leadData) address, and the Lead is valid for an update:
     * 1. Grab any events related to the lead.
     * 2. Set the next follow-up target date to the following day.
     * 3. Use engagement engine to determine the most ideal follow-up time.
     * 4. Generate the next follow-up date/time.
     * 5. Set Lead to Aged
     */
    private void processLeadResumption(LeadDataTransfer leadData, Address address, Lead lead) {
        if (isExistingAddress(address, leadData) && isValidForUpdate(lead)) {
            List<Event> eventList = eventRepo.findByLead(lead);

            LocalDate targetDate = LocalDate.now().plusDays(1);
            LocalTime targetTime = engagementEngine
                    .determineFollowUpTime(lead.getPreviousFollowUp(), targetDate, eventList);
            LocalDateTime nextFollowUp = LocalDateTime.of(targetDate, targetTime);

            lead.setNextFollowUp(nextFollowUp);
            lead.setStage(LeadStage.AGED_HIGH_PRIORITY.toString());

            Optional<Lead> optUpdatedLead = leadRepo.safeUpdate(lead);
            optUpdatedLead.ifPresent(updatedLead -> logger.info("Lead reentry. Update successful: {}", updatedLead));
        }
    }




    private void processNewAddressExistingContact(LeadDataTransfer leadData, Contact contact, Lead lead) {
        if (Validation.leadAddressIsValid(leadData)) {
            Optional<Address> optNewAddress = addressRepo.safeInsert(new Address(leadData));
            optNewAddress.ifPresent(newAddress -> {
                logger.info("Address Insert Successful. Address: {}", newAddress);
                Optional<Lead> optNewLead = leadRepo.safeInsert(new Lead(leadData, contact, newAddress));
                optNewLead.ifPresent(newLead -> logger.error("Lead Insert Successful. Lead: {}", lead));
            });
        }
    }




}
