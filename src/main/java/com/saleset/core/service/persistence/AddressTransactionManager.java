package com.saleset.core.service.persistence;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dto.LeadRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.service.persistence.leads.LeadWorkflowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressTransactionManager implements LeadWorkflowManager {

    private final AddressRepo addressRepo;
    private final Logger logger = LoggerFactory.getLogger(AddressTransactionManager.class);

    @Autowired
    public AddressTransactionManager(AddressRepo addressRepo) {
        this.addressRepo = addressRepo;
    }

    public Address resolveOrInsert(LeadRequest leadData) {
        if (!leadAddressIsValid(leadData)) return null;

        return addressRepo.findAddressByLeadDataMatch(leadData)
                .orElseGet(() -> {
                    Optional<Address> opt = addressRepo.safeInsert(new Address(leadData));
                    opt.ifPresent(address -> logger.info("Address inserted: {}", address));
                    return opt.orElse(null);
                });
    }

    public Optional<Address> findAddressMatch(LeadRequest leadData) {
        if (!leadAddressIsValid(leadData)) return Optional.empty();
        return addressRepo.findAddressByLeadDataMatch(leadData);
    }

    public Optional<Address> insertNewAddress(LeadRequest leadData) {
        if (!leadAddressIsValid(leadData)) return Optional.empty();

        Optional<Address> opt = addressRepo.safeInsert(new Address(leadData));
        opt.ifPresent(address -> logger.info("Address inserted successfully: {}", address));
        return opt;
    }

}
