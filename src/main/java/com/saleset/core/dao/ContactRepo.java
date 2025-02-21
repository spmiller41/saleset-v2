package com.saleset.core.dao;

import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ContactRepo {


    private static final Logger logger = LoggerFactory.getLogger(ContactRepo.class);
    private final EntityManager entityManager;


    @Autowired
    public ContactRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Contact> findContactById(int contactId) {
        try {
            String query = "SELECT c FROM Contact c WHERE c.id = :contactId";
            Contact contact = entityManager.createQuery(query, Contact.class)
                    .setParameter("contactId", contactId)
                    .getSingleResult();

            return Optional.of(contact);
        } catch (NoResultException ex) {
            logger.error("No Contact found with this id: {}", contactId);
            return Optional.empty();
        }
    }


    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Contact> safeInsert(Contact contact) {
        try {
            entityManager.persist(contact);

            // Ensure immediate DB sync
            entityManager.flush();

            return Optional.of(contact);
        } catch (PersistenceException ex) {
            logger.error("Insert failed. Contact: {} --- Message: {}", contact, ex.getMessage());
            return Optional.empty();
        }
    }


    /**
     * Finds an existing contact in the database by comparing the primary and secondary phone numbers
     * from the provided lead data with those stored in the database.
     * <p>
     * The method ensures:
     * - A query checks if the primary or secondary phone in the database matches the provided primary or secondary phone.
     * - If the secondary phone in the lead data is null, only the primary phone is used in the query.
     *
     * @param leadData The lead data containing primary and secondary phone numbers to search for.
     * @return An Optional containing the matching Contact if found, otherwise an empty Optional.
     */
    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Contact> findContactByPhone(LeadDataTransfer leadData) {
        try {
            String query = "SELECT c FROM Contact c WHERE c.primaryPhone = :primaryPhone " +
                    "OR c.secondaryPhone = :primaryPhone " +
                    (leadData.getSecondaryPhone() != null ?
                            "OR c.primaryPhone = :secondaryPhone OR c.secondaryPhone = :secondaryPhone" : "");

            TypedQuery<Contact> queryObj = entityManager.createQuery(query, Contact.class)
                    .setParameter("primaryPhone", leadData.getPrimaryPhone());

            if (leadData.getSecondaryPhone() != null) {
                queryObj.setParameter("secondaryPhone", leadData.getSecondaryPhone());
            }

            return Optional.ofNullable(queryObj.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }


}
