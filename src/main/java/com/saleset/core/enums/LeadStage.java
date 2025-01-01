package com.saleset.core.enums;

public enum LeadStage {

    NEW ("New"),
    AGED_LOW_PRIORITY ("Aged_Low_Priority"),
    AGED_HIGH_PRIORITY ("Aged_High_Priority"),
    RETARGETED_NO_SHOW ("Retargeted_No_Show"),
    RETARGETED_REHASH ("Retargeted_Rehash"),
    CONVERTED ("Converted"),
    DNC ("Do_Not_Call");

    private final String leadStatus;

    LeadStage(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    @Override
    public String toString() {
        return this.leadStatus;
    }

}
