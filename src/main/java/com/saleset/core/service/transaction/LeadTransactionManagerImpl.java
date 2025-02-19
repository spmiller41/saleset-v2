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
import com.saleset.core.enums.PhoneLineType;
import com.saleset.core.service.engine.EngagementEngineImpl;
import com.saleset.core.service.sms.TwilioManager;
import com.saleset.core.util.PhoneNumberNormalizer;
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

    private final Logger logger = LoggerFactory.getLogger(LeadTransactionManagerImpl.class);

    private final EngagementEngineImpl engagementEngine;
    private final ContactRepo contactRepo;
    private final AddressRepo addressRepo;
    private final LeadRepo leadRepo;
    private final EventRepo eventRepo;
    private final TwilioManager twilioManager;

    @Autowired
    public LeadTransactionManagerImpl(EngagementEngineImpl engagementEngine, ContactRepo contactRepo,
                                      AddressRepo addressRepo, LeadRepo leadRepo, EventRepo eventRepo, TwilioManager twilioManager) {
        this.engagementEngine = engagementEngine;
        this.contactRepo = contactRepo;
        this.addressRepo = addressRepo;
        this.leadRepo = leadRepo;
        this.eventRepo = eventRepo;
        this.twilioManager = twilioManager;
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
    @Override
    @Transactional
    public void manageLead(LeadDataTransfer leadData) {
        // 1: Validate and normalize phone numbers
        if (!validateAndNormalizePhones(leadData)) return;

        // 2: Lookup contact and process existing leads
        System.out.println("lookupContactAndProcessLeads is about to be called");
        Optional<Contact> optContact = lookupContactAndProcessLeads(leadData);
        if (optContact.isPresent()) return;

        // 3: Handle new contact and address processing
        System.out.println("insertNewContact is about to be called");
        Contact contact = insertNewContact(leadData);
        if (contact == null) return;

        System.out.println("processAddress is about to be called");
        Address address = processAddress(leadData, contact);

        // 4: Create and insert a new lead
        System.out.println("insertNewLead is about to be called");
        insertNewLead(leadData, contact, address);
    }




    /*
     * Validates and normalizes phone numbers for the given lead data.
     * If no valid phone numbers are found, the method returns false.
     * - If only the secondary number is provided, it is swapped to primary.
     * - Uses Twilio's Lookup API to validate and determine the type of phone numbers.
     * - Handles cases where secondary phone types can be null.
     *
     * @param leadData The lead data containing phone numbers.
     * @return true if at least one phone number is valid and normalized, false otherwise.
     */
    private boolean validateAndNormalizePhones(LeadDataTransfer leadData) {
        if (leadData.getPrimaryPhone() == null && leadData.getSecondaryPhone() == null) {
            logger.warn("Lead kicked due to no phone number. Lead: {}", leadData);
            return false;
        }

        // Swap secondary to primary if primary is missing
        if (leadData.getPrimaryPhone() == null && leadData.getSecondaryPhone() != null) {
            leadData.setPrimaryPhone(leadData.getSecondaryPhone());
            leadData.setSecondaryPhone(null);
        }

        // Normalize phone numbers
        Optional<String> optPrimary = PhoneNumberNormalizer.normalizeToE164(leadData.getPrimaryPhone());
        Optional<String> optSecondary = PhoneNumberNormalizer.normalizeToE164(leadData.getSecondaryPhone());

        if (optPrimary.isEmpty() && optSecondary.isEmpty()) return false;

        // Use Twilio lookup to validate numbers.
        optPrimary.ifPresent(primaryPhone -> {
            leadData.setPrimaryPhoneType(twilioManager.lookupPhoneNumber(primaryPhone).getType());
            if (leadData.getPrimaryPhoneType() != PhoneLineType.INVALID) leadData.setPrimaryPhone(primaryPhone);
        });

        optSecondary.ifPresent(secondaryPhone -> {
            leadData.setSecondaryPhoneType(twilioManager.lookupPhoneNumber(secondaryPhone).getType());
            if (leadData.getSecondaryPhoneType() != PhoneLineType.INVALID) leadData.setSecondaryPhone(secondaryPhone);
        });

        if (leadData.getPrimaryPhoneType() == PhoneLineType.INVALID &&
                (leadData.getSecondaryPhoneType() == PhoneLineType.INVALID || leadData.getSecondaryPhoneType() == null)) {
            logger.warn("Lead kicked due to no validated number {}", leadData);
            return false;
        }

        return true;
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
        Optional<Contact> optContact = contactRepo.findContactByPhone(leadData);

        optContact.ifPresent(contact -> {
            List<Lead> leadList = leadRepo.findLeadByContact(contact);

            if (leadList.isEmpty()) {
                logger.error("Contact exists without lead: {}", contact);
                return;
            }

            leadList.forEach(lead -> {
                if (LeadStage.DNC.toString().equalsIgnoreCase(lead.getCurrentStage())) {
                    logger.warn("Contact belongs to Lead on DNC. Kicking Lead.");
                    return;
                }

                if (lead.getAddressId() != null) {
                    addressRepo.findAddressByLeadDataMatch(leadData)
                            .ifPresentOrElse(
                                address -> processLeadResumption(leadData, address, lead),
                                () -> processNewAddressExistingContact(leadData, contact, lead)
                            );
                } else {
                    processLeadResumption(leadData, lead);
                }
            });
        });

        return optContact;
    }




    /*
     * Inserts a new contact based on the provided lead data.
     * If insertion fails, returns null.
     *
     * @param leadData The lead data containing contact details.
     * @return The newly inserted Contact object, or null if insertion fails.
     */
    private Contact insertNewContact(LeadDataTransfer leadData) {
        Optional<Contact> optNewContact = contactRepo.safeInsert(new Contact(leadData));
        if (optNewContact.isEmpty()) {
            logger.warn("Failed to insert contact for lead data: {}", leadData);
            return null;
        }

        Contact contact = optNewContact.get();
        logger.info("Contact inserted successfully: {}", contact);
        return contact;
    }




    /*
     * Processes the address for the given lead data and contact.
     * If the address is valid and does not exist, it inserts a new address.
     *
     * @param leadData The lead data containing address details.
     * @param contact  The contact associated with the address.
     * @return The processed Address object, or null if no valid address is found or inserted.
     */
    private Address processAddress(LeadDataTransfer leadData, Contact contact) {
        if (!leadAddressIsValid(leadData)) return null;

        return addressRepo.findAddressByLeadDataMatch(leadData)
                .orElseGet(() -> {
                    Optional<Address> optNewAddress = addressRepo.safeInsert(new Address(leadData));
                    optNewAddress.ifPresent(newAddress ->
                            logger.info("Address inserted successfully: {}", newAddress)
                    );
                    return optNewAddress.orElse(null);
                });
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
    private void processLeadResumption(LeadDataTransfer leadData, Lead lead) {
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
    private void processNewAddressExistingContact(LeadDataTransfer leadData, Contact contact, Lead lead) {
        if (leadAddressIsValid(leadData)) {
            Optional<Address> optNewAddress = addressRepo.safeInsert(new Address(leadData));
            optNewAddress.ifPresent(newAddress -> {
                logger.info("Address Insert Successful. Address: {}", newAddress);
                Optional<Lead> optNewLead = leadRepo.safeInsert(new Lead(leadData, contact, newAddress));
                optNewLead.ifPresent(newLead -> logger.info("Lead Insert Successful. Lead: {}", lead));
            });
        }
    }




}
