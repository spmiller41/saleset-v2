package com.saleset.core.enums;

public enum PhoneLineType {

    MOBILE("Mobile"),
    LANDLINE("Landline");

    private final String lineType;

    PhoneLineType(String lineType) { this.lineType = lineType; }

    @Override
    public String toString() { return this.lineType; }

}
