package com.saleset.core.dao;

import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventRepo {

    private static final Logger logger = LoggerFactory.getLogger(EventRepo.class);
    private final EntityManager entityManager;

    @Autowired
    public EventRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Event> safeInsert(Event event) {
        try {
            entityManager.persist(event);

            // Ensure immediate DB sync
            entityManager.flush();

            return Optional.of(event);
        } catch (PersistenceException ex) {
            logger.error("Insert failed. Event: {} --- Message: {}", event, ex.getMessage());
            return Optional.empty();
        }
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public List<Event> findByLead(Lead lead) {
        String query = "SELECT e FROM Event e WHERE e.leadId = :leadId";

        return entityManager.createQuery(query, Event.class)
                .setParameter("leadId", lead.getId())
                .getResultList();
    }

}
