package com.saleset.core.service.transaction.leads;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
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

import java.util.List;
import java.util.Optional;

@Service
public class LeadEntryPipelineManager implements LeadWorkflowManager {

    private final Logger logger = LoggerFactory.getLogger(LeadEntryPipelineManager.class);

    private final ContactTransactionManager contactTransactionManager;
    private final AddressTransactionManager addressTransactionManager;
    private final LeadRepo leadRepo;
    private final PhoneValidationService phoneValidationService;
    private final QueryUrlGenerator queryUrlGenerator;
    private final LeadEngagementManager leadEngagementManager;

    @Autowired
    public LeadEntryPipelineManager(EngagementEngineImpl engagementEngine,
                                    ContactTransactionManager contactTransactionManager,
                                    AddressTransactionManager addressTransactionManager,
                                    LeadRepo leadRepo,
                                    PhoneValidationService phoneValidationService,
                                    QueryUrlGenerator queryUrlGenerator, LeadEngagementManager leadEngagementManager) {
        this.leadEngagementManager = leadEngagementManager;
        this.contactTransactionManager = contactTransactionManager;
        this.addressTransactionManager = addressTransactionManager;
        this.leadRepo = leadRepo;
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
                    leadEngagementManager.updateLeadEngagementProcess(leadData, optAddress.get(), lead);
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
