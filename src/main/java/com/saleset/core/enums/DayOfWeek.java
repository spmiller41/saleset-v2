package com.saleset.core.enums;

public enum DayOfWeek {

    MONDAY ("Monday"),
    TUESDAY ("Tuesday"),
    WEDNESDAY ("Wednesday"),
    THURSDAY ("Thursday"),
    FRIDAY ("Friday"),
    SATURDAY ("Saturday"),
    SUNDAY ("Sunday");

    private final String dayOfWeek;

    DayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    @Override
    public String toString() { return this.dayOfWeek; }

}
