package com.saleset.core.service.sms;

import com.saleset.core.enums.PhoneLineType;

public class PhoneNumberDetails {

    private final PhoneLineType lineType;

    public PhoneNumberDetails(PhoneLineType lineType) {
        this.lineType = lineType;
    }

    public String getType() {
        return this.lineType.toString();
    }

}
