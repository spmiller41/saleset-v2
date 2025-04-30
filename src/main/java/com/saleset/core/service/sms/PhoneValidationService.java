package com.saleset.core.service.sms;

import com.saleset.core.dto.LeadDataTransfer;
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
    public boolean validateAndNormalizePhones(LeadDataTransfer leadData) {
        if (leadData.getPrimaryPhone() == null && leadData.getSecondaryPhone() == null) {
            logger.warn("Lead kicked due to no phone number. Lead: {}", leadData);
            return false;
        }

        // Swap secondary to primary if primary is missing
        if (leadData.getPrimaryPhone() == null && leadData.getSecondaryPhone() != null) {
            leadData.setPrimaryPhone(leadData.getSecondaryPhone());
            leadData.setSecondaryPhone(null);
        }

        // Normalize phone numbers
        Optional<String> optPrimary = PhoneNumberNormalizer.normalizeToE164(leadData.getPrimaryPhone());
        Optional<String> optSecondary = PhoneNumberNormalizer.normalizeToE164(leadData.getSecondaryPhone());

        if (optPrimary.isEmpty() && optSecondary.isEmpty()) return false;

        // Use Twilio lookup to validate numbers.
        optPrimary.ifPresent(primaryPhone -> {
            leadData.setPrimaryPhoneType(twilioManager.lookupPhoneNumber(primaryPhone).getType());
            if (leadData.getPrimaryPhoneType() != PhoneLineType.INVALID) leadData.setPrimaryPhone(primaryPhone);
        });

        optSecondary.ifPresent(secondaryPhone -> {
            leadData.setSecondaryPhoneType(twilioManager.lookupPhoneNumber(secondaryPhone).getType());
            if (leadData.getSecondaryPhoneType() != PhoneLineType.INVALID) leadData.setSecondaryPhone(secondaryPhone);
        });

        if (leadData.getPrimaryPhoneType() == PhoneLineType.INVALID &&
                (leadData.getSecondaryPhoneType() == PhoneLineType.INVALID || leadData.getSecondaryPhoneType() == null)) {
            logger.warn("Lead kicked due to no validated number {}", leadData);
            return false;
        }

        return true;
    }

}
