package com.saleset.core.engine;

import com.saleset.core.entities.Event;
import com.saleset.core.enums.PeriodOfDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface EngagementEngine {

    LocalTime determineFollowUpTime(LocalDateTime previousFollowUp, LocalDate targetDate, List<Event> leadEventList);
    LocalDate determineFollowUpDate(LocalDateTime fromDate, double follow_up_divisor);
    String determineNextStage(LocalDateTime fromDate, int maxDaysInStage);

    default String determineTargetDayOfWeek(LocalDate targetDate) {
        String normalizedDay = targetDate.getDayOfWeek().toString().toLowerCase();
        return normalizedDay.substring(0, 1).toUpperCase() + normalizedDay.substring(1);
    }

    default String determineTargetPeriodOfDay(LocalTime followUpTime) {
        if (followUpTime.isBefore(LocalTime.NOON)) {
            return PeriodOfDay.MORNING.toString();
        } else if (followUpTime.isBefore(LocalTime.of(18, 0))) {
            return PeriodOfDay.AFTERNOON.toString();
        } else {
            return PeriodOfDay.Evening.toString();
        }
    }

}
