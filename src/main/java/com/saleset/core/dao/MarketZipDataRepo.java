package com.saleset.core.dao;

import com.saleset.core.entities.Address;
import com.saleset.core.entities.MarketZipData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MarketZipDataRepo {

    private final EntityManager entityManager;

    @Autowired
    public MarketZipDataRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<MarketZipData> findByAddress(Address address) {
        String query = "SELECT mzd FROM MarketZipData mzd WHERE mzd.zip = :zipCode";

        try {
            MarketZipData data = entityManager.createQuery(query, MarketZipData.class)
                    .setParameter("zipCode", address.getZipCode())
                    .getSingleResult();
            return Optional.of(data);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
