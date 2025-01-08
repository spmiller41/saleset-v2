package com.saleset.core.service.transaction;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.LeadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeadTransactionManager {

    private ContactRepo contactRepo;
    private AddressRepo addressRepo;
    private LeadRepo leadRepo;

    @Autowired
    public LeadTransactionManager(ContactRepo contactRepo, AddressRepo addressRepo, LeadRepo leadRepo) {
        this.contactRepo = contactRepo;
        this.addressRepo = addressRepo;
        this.leadRepo = leadRepo;
    }



}
