package com.saleset.core.enums;

/**
 * LeadStage represents the various stages a lead can occupy in the engagement lifecycle.
 * Each stage may define a follow-up frequency divisor and a maximum number of days
 * the lead should remain in that stage.
 * <p>
 * Stages like CONVERTED and DNC are considered terminal and do not require further outreach.
 */
public enum LeadStage {

    NEW("New", 4.0, 5),
    AGED_LOW_PRIORITY("Aged_Low_Priority", 2.0, null),
    AGED_HIGH_PRIORITY("Aged_High_Priority", 0.75, 5),
    RETARGETED_NO_SHOW("Retargeted_No_Show", 2.0, 5),
    RETARGETED_REHASH("Retargeted_Rehash", 2.0, 5),
    CONVERTED("Converted", null, null),
    DNC("Do_Not_Call", null, null);

    private final String leadStatus;
    private final Double frequencyDivisor;
    private final Integer maxDaysInStage;

    LeadStage(String leadStatus, Double frequencyDivisor, Integer maxDaysInStage) {
        this.leadStatus = leadStatus;
        this.frequencyDivisor = frequencyDivisor;
        this.maxDaysInStage = maxDaysInStage;
    }

    /**
     * Converts a string to the corresponding LeadStage.
     *
     * @param status The string representation of the stage.
     * @return The matching LeadStage enum.
     * @throws IllegalArgumentException if no match is found.
     */
    public static LeadStage fromString(String status) {
        for (LeadStage stage : LeadStage.values()) if (stage.leadStatus.equalsIgnoreCase(status)) return stage;
        throw new IllegalArgumentException("Unknown lead stage: " + status);
    }

    public Double getFrequencyDivisor() {
        return frequencyDivisor;
    }

    public Integer getMaxDaysInStage() {
        return maxDaysInStage;
    }

    @Override
    public String toString() {
        return this.leadStatus;
    }

}
