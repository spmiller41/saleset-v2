package com.saleset.core.dao;

import com.saleset.core.entities.Appointment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AppointmentRepo {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentRepo.class);
    private final EntityManager entityManager;

    @Autowired
    public AppointmentRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Appointment> safeInsert(Appointment appointment) {
        try {
            entityManager.persist(appointment);

            // Ensure immediate DB sync
            entityManager.flush();

            return Optional.of(appointment);
        } catch (PersistenceException ex) {
            logger.error("Insert failed. Appointment: {} --- Message: {}", appointment, ex.getMessage());
            return Optional.empty();
        }
    }

}
