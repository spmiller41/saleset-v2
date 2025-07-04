package com.saleset.core.service.persistence.leads;

import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;

public interface LeadWorkflowManager {

    default boolean isExistingAddress(Address address, LeadRequest leadData) {
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
            lead.getCurrentStage().equalsIgnoreCase(LeadStage.NEW.toString()) ||
            lead.getCurrentStage().equalsIgnoreCase(LeadStage.AGED_HIGH_PRIORITY.toString()) ||
            lead.getCurrentStage().equalsIgnoreCase(LeadStage.RETARGETED_NO_SHOW.toString()) ||
            lead.getCurrentStage().equalsIgnoreCase(LeadStage.RETARGETED_REHASH.toString())
        );
    }

    default boolean leadAddressIsValid(LeadRequest leadData) {
        boolean isValid = leadData.getStreet() != null && leadData.getZipCode() != null;
        if (isValid) return true;

        // Fallback
        return leadData.getStreet() != null && leadData.getCity() != null && leadData.getState() != null;
    }

}
