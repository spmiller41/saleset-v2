package com.saleset.core.dao;

import com.saleset.core.entities.Address;
import com.saleset.core.entities.Lead;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LeadDao {

    private static final Logger logger = LoggerFactory.getLogger(LeadDao.class);
    private final EntityManager entityManager;

    @Autowired
    public LeadDao(EntityManager entityManager) {
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

}
