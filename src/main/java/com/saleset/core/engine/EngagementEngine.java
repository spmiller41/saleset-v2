package com.saleset.core.engine;

import com.saleset.core.entities.Event;
import com.saleset.core.enums.PeriodOfDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public interface EngagementEngine {

    LocalTime determineFollowUpTime(LocalDateTime previousFollowUp, LocalDate targetDate, List<Event> leadEventList);
    LocalDate determineFollowUpDate(LocalDateTime fromDate, double follow_up_divisor);
    String determineNextStage(LocalDateTime fromDate, int maxDaysInStage);

    default String determineTargetDayOfWeek(LocalDate targetDate) {
        String normalizedDay = targetDate.getDayOfWeek().toString().toLowerCase();
        return normalizedDay.substring(0, 1).toUpperCase() + normalizedDay.substring(1);
    }

    default String determineTargetPeriodOfDay(LocalTime previousFollowUpTime) {
        if (previousFollowUpTime.isBefore(LocalTime.NOON)) {
            return PeriodOfDay.Evening.toString();
        } else if (previousFollowUpTime.isBefore(LocalTime.of(18, 0))) {
            return PeriodOfDay.MORNING.toString();
        } else {
            return PeriodOfDay.AFTERNOON.toString();
        }
    }

    default List<Event> filterByTargetedDayOfWeek(String targetDayOfWeek, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> event.getDayOfWeek().equalsIgnoreCase(targetDayOfWeek))
                .collect(Collectors.toList());
    }

    default List<Event> filterByTargetedTimeOfDay(String targetPeriodOfDay, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> event.getPeriodOfDay().equalsIgnoreCase(targetPeriodOfDay))
                .collect(Collectors.toList());
    }

}
