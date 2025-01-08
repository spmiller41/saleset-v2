package com.saleset.core.dao;

import com.saleset.core.entities.Address;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AddressRepo {

    private static final Logger logger = LoggerFactory.getLogger(AddressRepo.class);
    private final EntityManager entityManager;

    @Autowired
    public AddressRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Address> safeInsert(Address address) {
        try {
            entityManager.persist(address);

            // Ensure immediate DB sync
            entityManager.flush();

            return Optional.of(address);
        } catch (PersistenceException ex) {
            logger.error("Insert failed. Address: {} --- Message: {}", address, ex.getMessage());
            return Optional.empty();
        }
    }

}
