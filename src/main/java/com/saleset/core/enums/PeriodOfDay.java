package com.saleset.core.enums;

public enum PeriodOfDay {

    MORNING ("Morning"),
    AFTERNOON ("Afternoon"),
    EVENING("Evening");

    private final String periodOfDay;

    PeriodOfDay(String periodOfDay) { this.periodOfDay = periodOfDay; }

    @Override
    public String toString() { return this.periodOfDay; }

}
