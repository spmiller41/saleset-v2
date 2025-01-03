package com.saleset.core.engine;

import com.saleset.core.entities.Event;
import com.saleset.core.enums.PeriodOfDay;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface EngagementEngine {


    LocalTime determineFollowUpTime(LocalDateTime previousFollowUp, LocalDate targetDate, List<Event> leadEventList);
    LocalDate determineFollowUpDate(LocalDateTime fromDate, double follow_up_divisor);
    String determineNextStage(LocalDateTime fromDate, int maxDaysInStage);


    default String determineTargetDayOfWeek(LocalDate targetDate) {
        return targetDate.getDayOfWeek().toString();
    }


    default PeriodOfDay determineTargetPeriodOfDay(LocalTime previousFollowUpTime) {
        if (previousFollowUpTime.isBefore(LocalTime.NOON)) return PeriodOfDay.EVENING;
        else if (previousFollowUpTime.isBefore(LocalTime.of(18, 0))) return PeriodOfDay.MORNING;
        else return PeriodOfDay.AFTERNOON;
    }


    default PeriodOfDay determinePeriodOfDay(LocalTime time) {
        if (time.isBefore(LocalTime.NOON)) return PeriodOfDay.MORNING;
        else if (time.isBefore(LocalTime.of(18, 0))) return PeriodOfDay.AFTERNOON;
        else return PeriodOfDay.EVENING;
    }


    default LocalTime determinePeakHour(PeriodOfDay periodOfDay) {
        LocalTime peakHour;

        if (periodOfDay.equals(PeriodOfDay.MORNING)) peakHour = LocalTime.of(10, 0);
        else if (periodOfDay.equals(PeriodOfDay.AFTERNOON)) peakHour = LocalTime.of(15, 0);
        else peakHour = LocalTime.of(18, 0);

        return peakHour;
    }


    default List<Event> filterByTimePeriod(int maxDays, List<Event> leadEventList) {
        LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(maxDays);

        return leadEventList.stream()
                .filter(event -> event.getCreatedAt().isAfter(cutoffDateTime))
                .collect(Collectors.toList());
    }


    default List<Event> filterByTargetedDayOfWeek(String targetDayOfWeek, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> event.getDayOfWeek().equalsIgnoreCase(targetDayOfWeek))
                .collect(Collectors.toList());
    }


    default List<Event> filterByTargetedPeriodOfDay(String targetPeriodOfDay, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> event.getPeriodOfDay().equalsIgnoreCase(targetPeriodOfDay))
                .collect(Collectors.toList());
    }


    default List<Event> filterByFallbackPeriodOfDay(PeriodOfDay previousPeriodOfDay, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> !event.getPeriodOfDay().equalsIgnoreCase(previousPeriodOfDay.toString()))
                .collect(Collectors.toList());
    }


    default LocalTime deviateByMinutes(LocalTime originalTime, int maxDeviationHours) {
        int maxDeviationMinutes = maxDeviationHours * 60; // Convert hours to minutes
        int randomDeviation = (int) (Math.random() * (2 * maxDeviationMinutes + 1)) - maxDeviationMinutes;
        return originalTime.plusMinutes(randomDeviation);
    }


    default LocalTime findClosestEventTime(List<Event> eventList, LocalTime targetTime) {
        return eventList.stream()
                .map(event -> event.getCreatedAt().toLocalTime()) // Convert createdAt to LocalTime
                .min(Comparator.comparingInt(time -> Math.toIntExact(Math.abs(Duration.between(time, targetTime).toMinutes()))))
                .orElse(targetTime); // Fallback to targetTime if no events are present
    }


}
