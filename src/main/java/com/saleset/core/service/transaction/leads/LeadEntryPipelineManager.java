package com.saleset.core.service.transaction.leads;

import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.engine.EngagementEngineImpl;
import com.saleset.core.service.sms.PhoneValidationService;
import com.saleset.core.service.transaction.AddressTransactionManager;
import com.saleset.core.service.transaction.ContactTransactionManager;
import com.saleset.core.util.QueryUrlGenerator;
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

/**
 * Handles the full transactional workflow for new or re-entering leads entering the system.
 * <p>
 * This service manages lead intake by:
 * <ul>
 *   <li>Validating and normalizing phone numbers using Twilio Lookup</li>
 *   <li>Checking for existing contacts and handling DNC or lead resumption</li>
 *   <li>Inserting new contacts, addresses, and leads when appropriate</li>
 *   <li>Determining follow-up schedules using the Engagement Engine</li>
 * </ul>
 * <p>
 * Designed to be the entry point for processing lead data upon initial submission.
 */
@Service
public class LeadEntryPipelineManager implements LeadWorkflowManager {

    private final Logger logger = LoggerFactory.getLogger(LeadEntryPipelineManager.class);

    private final EngagementEngineImpl engagementEngine;
    private final ContactTransactionManager contactTransactionManager;
    private final AddressTransactionManager addressTransactionManager;
    private final LeadRepo leadRepo;
    private final EventRepo eventRepo;
    private final PhoneValidationService phoneValidationService;
    private final QueryUrlGenerator queryUrlGenerator;

    @Autowired
    public LeadEntryPipelineManager(EngagementEngineImpl engagementEngine,
                                    ContactTransactionManager contactTransactionManager,
                                    AddressTransactionManager addressTransactionManager,
                                    LeadRepo leadRepo,
                                    EventRepo eventRepo,
                                    PhoneValidationService phoneValidationService,
                                    QueryUrlGenerator queryUrlGenerator) {
        this.engagementEngine = engagementEngine;
        this.contactTransactionManager = contactTransactionManager;
        this.addressTransactionManager = addressTransactionManager;
        this.leadRepo = leadRepo;
        this.eventRepo = eventRepo;
        this.phoneValidationService = phoneValidationService;
        this.queryUrlGenerator = queryUrlGenerator;
    }




    /**
     * Manages the processing of a lead, including phone number validation, contact lookup,
     * address handling, and lead creation or resumption.
     * <p>
     * Steps:
     * 1. Validates and normalizes phone numbers from the lead data.
     * 2. Looks up an existing contact and processes associated leads if found.
     * 3. If no contact exists, inserts a new contact and handles address processing.
     * 4. Creates and inserts a new lead if necessary.
     * <p>
     * @param leadData The data transfer object containing lead details such as phone, address, and contact information.
     */
    @Transactional
    public void manageLead(LeadDataTransfer leadData) {
        // 1: Validate and normalize phone numbers
        if (!phoneValidationService.validateAndNormalizePhones(leadData)) return;

        // 2: Lookup contact and process existing leads
        Optional<Contact> optContact = lookupContactAndProcessLeads(leadData);
        if (optContact.isPresent()) return;

        // 3: Handle new contact and address processing
        Contact contact = contactTransactionManager.insertContact(leadData);
        if (contact == null) return;

        Address address = addressTransactionManager.resolveOrInsert(leadData);

        // 4: Create and insert a new lead
        insertNewLead(leadData, contact, address);
    }



    /*
     * Looks up a contact based on the phone numbers provided in the lead data.
     * If a contact is found, it processes the associated leads, checking for:
     * - DNC (Do Not Contact) status
     * - Address matches for lead resumption or new lead creation.
     *
     * @param leadData The lead data containing phone numbers and address information.
     * @return An Optional containing the found contact, or empty if no contact is found.
     */
    private Optional<Contact> lookupContactAndProcessLeads(LeadDataTransfer leadData) {
        Optional<Contact> optContact = contactTransactionManager.findByPhone(leadData);

        optContact.ifPresent(contact -> {
            List<Lead> leadList = leadRepo.findLeadByContact(contact);

            if (leadList.isEmpty()) {
                logger.error("Contact exists without lead: {}", contact);
                return;
            }

            Optional<Address> optAddress = addressTransactionManager.findAddressMatch(leadData);
            boolean payloadHasAddress = leadAddressIsValid(leadData);
            boolean nullAddressLeadExists = leadList.stream().anyMatch(lead -> lead.getAddressId() == null);

            for (Lead lead : leadList) {
                if (LeadStage.DNC.toString().equalsIgnoreCase(lead.getCurrentStage())) {
                    logger.warn("Contact belongs to Lead on DNC. Kicking Lead.");
                    return;
                }

                if (optAddress.isPresent() && lead.getAddressId() != null &&
                        lead.getAddressId().equals(optAddress.get().getId())) {
                    processLeadResumption(leadData, optAddress.get(), lead);
                    return;
                }
            }

            if (!payloadHasAddress && nullAddressLeadExists) {
                logger.info("Duplicate null-address lead detected. Skipping insert.");
                return;
            }

            if (optAddress.isPresent()) {
                insertNewLead(leadData, contact, optAddress.get());
            } else if (payloadHasAddress) {
                processNewAddressExistingContact(leadData, contact);
            } else {
                insertNewLead(leadData, contact, null);
            }
        });

        return optContact;
    }



    
    /*
     * Creates and inserts a new lead with the given lead data, contact, and address.
     * If the address is null, the lead is created without an associated address.
     *
     * @param leadData The lead data containing lead details.
     * @param contact  The contact associated with the lead.
     * @param address  The address associated with the lead (can be null).
     */
    private void insertNewLead(LeadDataTransfer leadData, Contact contact, Address address) {
        Lead lead = (address != null)
                ? new Lead(leadData, contact, address)
                : new Lead(leadData, contact);
        lead.setBookingPageUrl(queryUrlGenerator.buildBooking(lead, contact, address));
        lead.setTrackingWebhookUrl(queryUrlGenerator.buildTracking(lead));

        Optional<Lead> optLead = leadRepo.safeInsert(lead);
        optLead.ifPresent(newLead -> logger.info("Lead inserted successfully: {}", newLead));
    }




    /*
     * Handles leads that could possibly reenter the system.
     * If Address is found via ID and matches payload (leadData) address, and the Lead is valid for an update:
     * 1. Grab any events related to the lead.
     * 2. Set the next follow-up target date to the following day.
     * 3. Use engagement engine to determine the most ideal follow-up time.
     * 4. Generate the next follow-up date/time.
     * 5. Set Lead to AGED_HIGH_PRIORITY
     */
    private void processLeadResumption(LeadDataTransfer leadData, Address address, Lead lead) {
        if (isExistingAddress(address, leadData) && isValidForUpdate(lead)) {
            List<Event> eventList = eventRepo.findByLead(lead);

            LocalDate targetDate = LocalDate.now().plusDays(1);
            LocalTime targetTime = engagementEngine
                    .determineFollowUpTime(lead.getPreviousFollowUp(), targetDate, eventList);
            LocalDateTime nextFollowUp = LocalDateTime.of(targetDate, targetTime);

            lead.setNextFollowUp(nextFollowUp);
            lead.setCurrentStage(LeadStage.AGED_HIGH_PRIORITY.toString());

            Optional<Lead> optUpdatedLead = leadRepo.safeUpdate(lead);
            optUpdatedLead.ifPresent(updatedLead -> logger.info("Lead reentry - Contains Address. Update successful: {}", updatedLead));
        }
    }




    /*
     * Handles leads that reenter the system without an existing address validation.
     * If the Lead is valid for an update:
     * 1. Retrieve any events related to the lead.
     * 2. Set the next follow-up target date to the following day.
     * 3. Use the engagement engine to determine the ideal follow-up time.
     * 4. Generate the next follow-up date and time.
     * 5. Update the Lead stage to "Aged High Priority."
     */
    private void processLeadResumption(Lead lead) {
        if (isValidForUpdate(lead)) {
            List<Event> eventList = eventRepo.findByLead(lead);

            LocalDate targetDate = LocalDate.now().plusDays(1);
            LocalTime targetTime = engagementEngine
                    .determineFollowUpTime(lead.getPreviousFollowUp(), targetDate, eventList);
            LocalDateTime nextFollowUp = LocalDateTime.of(targetDate, targetTime);

            lead.setNextFollowUp(nextFollowUp);
            lead.setCurrentStage(LeadStage.AGED_HIGH_PRIORITY.toString());

            Optional<Lead> optUpdatedLead = leadRepo.safeUpdate(lead);
            optUpdatedLead.ifPresent(updatedLead -> logger.info("Lead reentry - No Address Update successful: {}", updatedLead));
        }
    }




    /*
     * This method processes a new address for an existing contact.
     * It validates the address from lead data, attempts to insert the address into the database,
     * and then creates a new lead associated with the contact and the inserted address.
     */
    private void processNewAddressExistingContact(LeadDataTransfer leadData, Contact contact) {
        addressTransactionManager.insertNewAddress(leadData)
                .ifPresent(newAddress -> {
                    logger.info("Address Insert Successful. Address: {}", newAddress);
                    insertNewLead(leadData, contact, newAddress);
                });
    }



}
