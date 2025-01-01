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

import java.util.Optional;

@Repository
public class EventDao {

    private static final Logger logger = LoggerFactory.getLogger(EventDao.class);
    private final EntityManager entityManager;

    @Autowired
    public EventDao(EntityManager entityManager) {
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

}
