package com.saleset.core.service.transaction;

import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactTransactionManager {

    private final Logger logger = LoggerFactory.getLogger(ContactTransactionManager.class);
    private final ContactRepo contactRepo;

    @Autowired
    public ContactTransactionManager(ContactRepo contactRepo) {
        this.contactRepo = contactRepo;
    }

    public Optional<Contact> findByPhone(LeadDataTransfer leadData) {
        return contactRepo.findContactByPhone(leadData);
    }

    public Contact insertContact(LeadDataTransfer leadData) {
        Optional<Contact> optContact = contactRepo.safeInsert(new Contact(leadData));
        if (optContact.isEmpty()) {
            logger.warn("Failed to insert contact for lead data: {}", leadData);
            return null;
        }
        Contact contact = optContact.get();
        logger.info("Contact inserted successfully: {}", contact);
        return contact;
    }

}
