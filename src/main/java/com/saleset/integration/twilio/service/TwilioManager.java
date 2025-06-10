package com.saleset.integration.twilio.service;

import com.saleset.core.enums.PhoneLineType;
import com.saleset.integration.twilio.dto.PhoneNumberDetails;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.lookups.v2.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioManager {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    public Message sendSMS(String from, String to, String messageBody) {
        Twilio.init(accountSid, authToken);

        return Message.creator(
                        new com.twilio.type.PhoneNumber(to),
                        new com.twilio.type.PhoneNumber(from),
                        messageBody)
                .create();
    }

    /**
     * Looks up a phone number using the Twilio API and categorizes it as either mobile or landline based on its line type.
     * <p>
     * This method initializes Twilio with the account credentials, fetches the phone number details including
     * line type intelligence, and then categorizes the line type as 'mobile' or 'landline'. If the phone number is not
     * valid or if the Twilio API returns a 404 error, the method returns null.
     *
     * @param phoneNumber The phone number to be looked up.
     * @return PhoneNumberDetails with the categorized line type or null if the number is invalid or not found.
     * @throws com.twilio.exception.ApiException if there are issues contacting the Twilio API beyond a 404 error.
     */
    public PhoneNumberDetails lookupPhoneNumber(String phoneNumber) {
        try {
            Twilio.init(accountSid, authToken);
            PhoneNumber number = PhoneNumber.fetcher(phoneNumber).setFields("line_type_intelligence").fetch();

            // Return null if number is not valid or essential data is missing
            if (!number.getValid() || number.getLineTypeIntelligence() == null ||
                    number.getLineTypeIntelligence().get("type") == null) return new PhoneNumberDetails(PhoneLineType.INVALID);

            PhoneLineType lineType = categorizeLineType(number.getLineTypeIntelligence().get("type").toString());
            return new PhoneNumberDetails(lineType);
        } catch (com.twilio.exception.ApiException twilioApiException) {
            if (twilioApiException.getStatusCode() == 404) return new PhoneNumberDetails(PhoneLineType.INVALID);
            else throw twilioApiException;
        }
    }

    private PhoneLineType categorizeLineType(String lineType) {
        if ("mobile".equalsIgnoreCase(lineType) ||
                "fixedVoip".equalsIgnoreCase(lineType) ||
                "nonFixedVoip".equalsIgnoreCase(lineType) ||
                "personal".equalsIgnoreCase(lineType) ||
                "voicemail".equalsIgnoreCase(lineType) ||
                "unknown".equalsIgnoreCase(lineType)) {
            return PhoneLineType.MOBILE;
        } else {
            return PhoneLineType.LANDLINE;
        }
    }

}
