package com.saleset.core.service.transaction;

import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;

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

    default boolean isValidForUpdate(Lead lead) {
        return !(
            lead.getStage().equalsIgnoreCase(LeadStage.NEW.toString()) ||
            lead.getStage().equalsIgnoreCase(LeadStage.AGED_HIGH_PRIORITY.toString()) ||
            lead.getStage().equalsIgnoreCase(LeadStage.RETARGETED_NO_SHOW.toString()) ||
            lead.getStage().equalsIgnoreCase(LeadStage.RETARGETED_REHASH.toString())
        );
    }

}
