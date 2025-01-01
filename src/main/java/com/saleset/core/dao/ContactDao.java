package com.saleset.core.dao;

import com.saleset.core.entities.Contact;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ContactDao {

    private static final Logger logger = LoggerFactory.getLogger(ContactDao.class);
    private final EntityManager entityManager;

    @Autowired
    public ContactDao(EntityManager entityManager) {
        this.entityManager = entityManager;
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

}
