package com.saleset.core.util;

import com.saleset.core.enums.PeriodOfDay;

import java.time.LocalTime;

public class TimePeriodIdentifier {

    public static PeriodOfDay identifyPeriodOfDay(LocalTime time) {
        if (time.isBefore(LocalTime.NOON)) return PeriodOfDay.MORNING;
        else if (time.isBefore(LocalTime.of(18, 0))) return PeriodOfDay.AFTERNOON;
        else return PeriodOfDay.EVENING;
    }

}
