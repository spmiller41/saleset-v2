package com.saleset.core.dao;

import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LeadRepo {

    private static final Logger logger = LoggerFactory.getLogger(LeadRepo.class);
    private final EntityManager entityManager;

    @Autowired
    public LeadRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Lead> safeInsert(Lead lead) {
        try {
            entityManager.persist(lead);

            // Ensure immediate DB sync
            entityManager.flush();

            return Optional.of(lead);
        } catch (PersistenceException ex) {
            logger.error("Insert failed. Lead: {} --- Message: {}", lead, ex.getMessage());
            return Optional.empty();
        }
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Lead> findLeadById(int leadId) {
        try {
            String query = "SELECT l FROM Lead l WHERE l.id = :leadId";
            Lead lead = entityManager.createQuery(query, Lead.class)
                    .setParameter("leadId", leadId)
                    .getSingleResult();

            return Optional.of(lead);
        } catch (NoResultException ex) {
            logger.error("No Lead found with this id: {}", leadId);
            return Optional.empty();
        }
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public List<Lead> findLeadByContact(Contact contact) {
        String query = "SELECT l FROM Lead l WHERE l.contactId = :contactId";

        return entityManager.createQuery(query, Lead.class)
                .setParameter("contactId", contact.getId())
                .getResultList();
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Lead> safeUpdate(Lead lead) {
        try {
            Lead updateLead = entityManager.merge(lead);

            // Ensure immediate DB sync
            entityManager.flush();

            return Optional.of(updateLead);
        } catch (PersistenceException ex) {
            logger.error("Update failed. Lead: {} --- Message: {}", lead, ex.getMessage());
            return Optional.empty();
        }
    }

}
