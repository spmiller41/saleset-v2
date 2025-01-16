package com.saleset.core.service.transaction;

import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;

public interface LeadTransactionManager {

    void manageLead(LeadDataTransfer leadData);

    default boolean isExistingAddress(Address address, LeadDataTransfer leadData) {
        boolean addressMatch = address.getStreet().equalsIgnoreCase(leadData.getStreet())
                && address.getZipCode().equalsIgnoreCase(leadData.getZipCode());
        if (addressMatch) return true;

        // Fallback
        return address.getStreet().equalsIgnoreCase(leadData.getStreet())
                && address.getCity().equalsIgnoreCase(leadData.getCity())
                && address.getState().equalsIgnoreCase(leadData.getState());
    }

}
