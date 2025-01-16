package com.saleset.core.util;

import com.saleset.core.dto.LeadDataTransfer;

public class Validation {

    public static boolean leadAddressIsValid(LeadDataTransfer leadData) {
        boolean isValid = leadData.getStreet() != null && leadData.getZipCode() != null;
        if (isValid) return true;

        // Fallback
        return leadData.getStreet() != null && leadData.getCity() != null && leadData.getState() != null;
    }

}
