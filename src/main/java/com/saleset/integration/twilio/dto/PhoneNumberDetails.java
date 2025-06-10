package com.saleset.integration.twilio.dto;

import com.saleset.core.enums.PhoneLineType;

public class PhoneNumberDetails {

    private final PhoneLineType lineType;

    public PhoneNumberDetails(PhoneLineType lineType) {
        this.lineType = lineType;
    }

    public PhoneLineType getType() {
        return this.lineType;
    }

}
