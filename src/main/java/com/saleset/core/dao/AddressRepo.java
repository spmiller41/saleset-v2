package com.saleset.core.dao;

import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Lead;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Address> findAddressByLead(Lead lead) {
        String query = "SELECT a FROM Address a WHERE a.id = :addressId";

        try {
            return Optional.of(entityManager.createQuery(query, Address.class)
                    .setParameter("addressId", lead.getAddressId())
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    // Remove Transactional and use at service layer after testing.
    @Transactional
    public Optional<Address> findAddressByLeadDataMatch(LeadDataTransfer leadData) {
        String query = "SELECT a FROM Address a WHERE " +
                "(a.street = :street AND a.zipCode = :zipCode) OR " +
                "(a.street = :street AND a.city = :city AND a.state = :state)";

        try {
            Address address = entityManager.createQuery(query, Address.class)
                    .setParameter("street", leadData.getStreet())
                    .setParameter("zipCode", leadData.getZipCode())
                    .setParameter("city", leadData.getCity())
                    .setParameter("state", leadData.getState())
                    .getSingleResult();

            return Optional.of(address);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
