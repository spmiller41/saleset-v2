package com.saleset.core.service.persistence.leads;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.engine.EngagementEngineImpl;
import com.saleset.core.service.outreach.task.TaskConfig;
import com.saleset.integration.shorten.UrlShortenerImpl;
import com.saleset.integration.twilio.service.PhoneValidationService;
import com.saleset.core.service.persistence.AddressTransactionManager;
import com.saleset.core.service.persistence.ContactTransactionManager;
import com.saleset.core.util.QueryUrlGenerator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing the entry pipeline of new and returning leads.
 * Handles phone validation, contact lookup, address resolution, and lead creation or engagement updates.
 * <p>
 * Designed to support re-engagement of existing contacts, prevent duplicates,
 * and ensure scalable lead intake with clean fallback logic.
 */
@Service
public class LeadEntryPipelineManager implements LeadWorkflowManager {

    private final Logger logger = LoggerFactory.getLogger(LeadEntryPipelineManager.class);

    private final ContactTransactionManager contactTransactionManager;
    private final AddressTransactionManager addressTransactionManager;
    private final LeadRepo leadRepo;
    private final PhoneValidationService phoneValidationService;
    private final QueryUrlGenerator queryUrlGenerator;
    private final LeadEngagementManager leadEngagementManager;
    private final TaskConfig taskConfig;
    private final UrlShortenerImpl urlShortener;

    @Autowired
    public LeadEntryPipelineManager(EngagementEngineImpl engagementEngine,
                                    ContactTransactionManager contactTransactionManager,
                                    AddressTransactionManager addressTransactionManager,
                                    LeadRepo leadRepo,
                                    PhoneValidationService phoneValidationService,
                                    QueryUrlGenerator queryUrlGenerator,
                                    LeadEngagementManager leadEngagementManager,
                                    TaskConfig taskConfig, UrlShortenerImpl urlShortener) {
        this.leadEngagementManager = leadEngagementManager;
        this.contactTransactionManager = contactTransactionManager;
        this.addressTransactionManager = addressTransactionManager;
        this.leadRepo = leadRepo;
        this.phoneValidationService = phoneValidationService;
        this.queryUrlGenerator = queryUrlGenerator;
        this.taskConfig = taskConfig;
        this.urlShortener = urlShortener;
    }




    /**
     * Public entrypoint for managing lead processing logic.
     * Validates phone numbers, checks for existing contacts, and inserts new leads if necessary.
     *
     * @param leadData The incoming lead data submission.
     */
    @Transactional
    public void manageLead(LeadRequest leadData) {
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
     * Attempts to locate an existing contact by phone and determine if the lead should be re-engaged or skipped.
     * If matched, performs address validation, duplication checks, and lead engagement updates.
     *
     * @param leadData The incoming lead data.
     * @return An Optional containing the matched contact, or empty if not found.
     */
    private Optional<Contact> lookupContactAndProcessLeads(LeadRequest leadData) {
        Optional<Contact> optContact = contactTransactionManager.findByPhone(leadData);

        optContact.ifPresent(contact -> {
            List<Lead> leadList = leadRepo.findLeadByContact(contact);

            if (handleContactWithoutLead(contact, leadList)) return;

            Optional<Address> optAddress = addressTransactionManager.findAddressMatch(leadData);
            Address address = optAddress.orElse(null);

            boolean payloadHasAddress = leadAddressIsValid(leadData);
            boolean nullAddressLeadExists = checkForNullAddressLead(leadList);

            if (handleDncOrMatchedAddress(leadData, address, leadList)) return;

            if (handleNullAddressDuplicate(payloadHasAddress, nullAddressLeadExists)) return;

            handleLeadInsertions(leadData, contact, address, payloadHasAddress);
        });

        return optContact;
    }




    /*
     * Creates and inserts a new Lead entity tied to the provided contact and address.
     * Populates booking and tracking URLs using the query generator.
     * Optionally supports shortening URLs (may be commented out for performance/testing).
     *
     * @param leadData The incoming data transfer object containing lead details.
     * @param contact  The contact to associate the lead with.
     * @param address  The address to associate the lead with (nullable).
     */
    private void insertNewLead(LeadRequest leadData, Contact contact, Address address) {
        LocalDateTime scheduledOutreach = LocalDateTime.now().plusMinutes(taskConfig.getFollowUpWindowMinutes() + 1);

        Lead lead = (address != null)
                ? new Lead(leadData, contact, address, scheduledOutreach)
                : new Lead(leadData, contact, scheduledOutreach);

        lead.setBookingPageUrl(queryUrlGenerator.buildBooking(lead, contact, address));
        lead.setTrackingWebhookUrl(queryUrlGenerator.buildTracking(lead));

        String shortUrlTracking = urlShortener.shorten(lead.getTrackingWebhookUrl(), "sms event tracking");
        String shortUrlBooking = urlShortener.shorten(lead.getBookingPageUrl(), "booking page");

        lead.setTrackingWebhookUrl(shortUrlTracking);
        lead.setBookingPageUrl(shortUrlBooking);

        Optional<Lead> optLead = leadRepo.safeInsert(lead);
        optLead.ifPresent(newLead -> logger.info("Lead inserted successfully: {}", newLead));
    }




    /*
     * Inserts a new address for an existing contact and immediately creates a lead tied to it.
     * Logs the address insertion before continuing with lead creation.
     */
    private void processNewAddressExistingContact(LeadRequest leadData, Contact contact) {
        addressTransactionManager.insertNewAddress(leadData)
                .ifPresent(newAddress -> {
                    logger.info("Address Insert Successful. Address: {}", newAddress);
                    insertNewLead(leadData, contact, newAddress);
                });
    }




    /*
     * Handles the edge case where a contact exists but has no associated leads.
     * Logs an error and returns true to prevent further processing.
     */
    private boolean handleContactWithoutLead(Contact contact, List<Lead> leadList) {
        if (leadList.isEmpty()) {
            logger.error("Contact exists without lead: {}", contact);
            return true;
        }
        return false;
    }




    /*
     * Checks if any lead is on the DNC list or has a matching/null address to trigger re-engagement logic.
     * If conditions match, updates lead engagement and halts further insert logic.
     */
    private boolean handleDncOrMatchedAddress(LeadRequest leadData, Address address, List<Lead> leadList) {
        for (Lead lead : leadList) {
            if (LeadStage.DNC.toString().equalsIgnoreCase(lead.getCurrentStage())) {
                logger.warn("Contact belongs to Lead on DNC. Kicking Lead.");
                return true;
            }

            if (address != null && lead.getAddressId() != null &&
                    lead.getAddressId().equals(address.getId())) {
                leadEngagementManager.updateEngagementOnLeadResumption(leadData, address, lead);
                return true;
            }

            if (address == null && lead.getAddressId() == null && !leadAddressIsValid(leadData)) {
                leadEngagementManager.updateEngagementOnLeadResumption(lead);
                return true;
            }
        }
        return false;
    }




    /*
     * Prevents inserting a second null-address lead for a contact.
     * Returns true if one already exists and the payload has no address.
     */
    private boolean handleNullAddressDuplicate(boolean payloadHasAddress, boolean nullAddressLeadExists) {
        if (!payloadHasAddress && nullAddressLeadExists) {
            logger.info("Duplicate null-address lead detected. Skipping insert.");
            return true;
        }
        return false;
    }




    /*
     * Checks if the lead list contains any leads that do not have an address ID assigned.
     *
     * @param leadList List of leads tied to a contact.
     * @return true if any lead has a null address ID.
     */
    private boolean checkForNullAddressLead(List<Lead> leadList) {
        return leadList.stream().anyMatch(lead -> lead.getAddressId() == null);
    }




    /*
     * Determines the appropriate insertion path based on whether an address is present or needs to be created.
     * Supports three outcomes: insert with address, insert new address then lead, or insert without address.
     */
    private void handleLeadInsertions(LeadRequest leadData, Contact contact,
                                      Address address, boolean payloadHasAddress) {
        if (address != null) {
            insertNewLead(leadData, contact, address);
        } else if (payloadHasAddress) {
            processNewAddressExistingContact(leadData, contact);
        } else {
            insertNewLead(leadData, contact, null);
        }
    }




}
