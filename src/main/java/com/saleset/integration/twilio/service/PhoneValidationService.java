package com.saleset.integration.twilio.service;

import com.saleset.core.dto.LeadRequest;
import com.saleset.core.enums.PhoneLineType;
import com.saleset.core.util.PhoneNumberNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PhoneValidationService {

    private final Logger logger = LoggerFactory.getLogger(PhoneValidationService.class);

    private final TwilioManager twilioManager;

    @Autowired
    public PhoneValidationService(TwilioManager twilioManager) { this.twilioManager = twilioManager; }




    /**
     * Validates and normalizes phone numbers for the given lead data.
     * If no valid phone numbers are found, the method returns false.
     * - If only the secondary number is provided, it is swapped to primary.
     * - Uses Twilio's Lookup API to validate and determine the type of phone numbers.
     * - Handles cases where secondary phone types can be null.
     *
     * @param leadData The lead data containing phone numbers.
     * @return true if at least one phone number is valid and normalized, false otherwise.
     */
    public boolean validateAndNormalizePhones(LeadRequest leadData) {
        if (leadData.getPrimaryPhone() == null && leadData.getSecondaryPhone() == null) {
            logger.warn("Lead kicked due to no phone number. Lead: {}", leadData);
            return false;
        }

        swapSecondaryToPrimaryIfNeeded(leadData);
        normalizeAndSetPhoneTypes(leadData);

        if (!hasValidPhoneType(leadData)) {
            logger.warn("Lead kicked due to no validated number {}", leadData);
            return false;
        }

        return true;
    }




    private void swapSecondaryToPrimaryIfNeeded(LeadRequest leadData) {
        if (leadData.getPrimaryPhone() == null && leadData.getSecondaryPhone() != null) {
            leadData.setPrimaryPhone(leadData.getSecondaryPhone());
            leadData.setSecondaryPhone(null);
        }
    }




    private void normalizeAndSetPhoneTypes(LeadRequest leadData) {
        Optional<String> optPrimary = PhoneNumberNormalizer.normalizeToE164(leadData.getPrimaryPhone());
        Optional<String> optSecondary = PhoneNumberNormalizer.normalizeToE164(leadData.getSecondaryPhone());

        optPrimary.ifPresent(primaryPhone -> {
            leadData.setPrimaryPhoneType(twilioManager.lookupPhoneNumber(primaryPhone).getType());
            if (leadData.getPrimaryPhoneType() != PhoneLineType.INVALID) {
                leadData.setPrimaryPhone(primaryPhone);
            }
        });

        optSecondary.ifPresent(secondaryPhone -> {
            leadData.setSecondaryPhoneType(twilioManager.lookupPhoneNumber(secondaryPhone).getType());
            if (leadData.getSecondaryPhoneType() != PhoneLineType.INVALID) {
                leadData.setSecondaryPhone(secondaryPhone);
            }
        });
    }




    private boolean hasValidPhoneType(LeadRequest leadData) {
        return leadData.getPrimaryPhoneType() != PhoneLineType.INVALID ||
                (leadData.getSecondaryPhoneType() != PhoneLineType.INVALID && leadData.getSecondaryPhoneType() != null);
    }

}