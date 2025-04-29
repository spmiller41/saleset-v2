package com.saleset.core.service.engine;

import com.saleset.core.entities.Event;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.enums.PeriodOfDay;
import com.saleset.core.util.TimePeriodIdentifier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The EngagementEngine interface defines the core logic for determining follow-up times,
 * dates, and lead stages based on a lead's interaction history. It includes methods for
 * filtering, processing, and analyzing event data to ensure effective and context-aware
 * engagement strategies.
 * <p>
 * This interface provides both abstract methods for key business logic and default methods
 * for utility operations, such as filtering events and deviating times.
 * <p>
 * Implementations of this interface should encapsulate business logic while keeping the
 * service layer focused on CRUD operations and transactions.
 */
public interface EngagementEngine {


    LocalTime determineFollowUpTime(LocalDateTime previousFollowUp, LocalDate targetDate, List<Event> leadEventList);
    LocalDate determineFollowUpDate(LocalDateTime fromDate, double follow_up_divisor);
    LeadStage determineNextStage(LocalDateTime stageUpdatedAt, int maxDaysInStage, LeadStage currentStage, LeadStage originalStage);


    /**
     * Determines the day of the week for the given target date.
     *
     * @param targetDate The date to retrieve the day of the week for.
     * @return A string representation of the day of the week (e.g., "MONDAY").
     */
    default String determineTargetDayOfWeek(LocalDate targetDate) {
        return targetDate.getDayOfWeek().toString();
    }


    /**
     * Determines the next target period of the day for follow-up based on the previous follow-up time.
     *
     * @param previousFollowUpTime The time of the previous follow-up.
     * @return The next target period of the day (MORNING, AFTERNOON, or EVENING).
     */
    default PeriodOfDay determineTargetPeriodOfDay(LocalTime previousFollowUpTime) {
        if (previousFollowUpTime.isBefore(LocalTime.NOON)) return PeriodOfDay.EVENING;
        else if (previousFollowUpTime.isBefore(LocalTime.of(18, 0))) return PeriodOfDay.MORNING;
        else return PeriodOfDay.AFTERNOON;
    }


    /**
     * Determines the period of the day (MORNING, AFTERNOON, or EVENING) for the given time.
     *
     * @param time The time to evaluate.
     * @return The period of the day corresponding to the given time.
     */
    default PeriodOfDay determinePeriodOfDay(LocalTime time) { return TimePeriodIdentifier.identifyPeriodOfDay(time); }


    /**
     * Determines the peak hour for a given period of the day.
     *
     * @param periodOfDay The period of the day (MORNING, AFTERNOON, or EVENING).
     * @return The peak hour as a LocalTime (e.g., 10:00 AM for MORNING).
     */
    default LocalTime determinePeakHour(PeriodOfDay periodOfDay) {
        LocalTime peakHour;

        if (periodOfDay.equals(PeriodOfDay.MORNING)) peakHour = LocalTime.of(10, 0);
        else if (periodOfDay.equals(PeriodOfDay.AFTERNOON)) peakHour = LocalTime.of(15, 0);
        else peakHour = LocalTime.of(19, 0);

        return peakHour;
    }


    /**
     * Filters a list of events to include only those within the last specified number of days.
     *
     * @param maxDays       The maximum number of days to look back.
     * @param leadEventList The list of events to filter.
     * @return A filtered list of events that occurred within the last maxDays.
     */
    default List<Event> filterByTimePeriod(int maxDays, List<Event> leadEventList) {
        LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(maxDays);

        return leadEventList.stream()
                .filter(event -> event.getCreatedAt().isAfter(cutoffDateTime))
                .collect(Collectors.toList());
    }


    /**
     * Filters a list of events to include only those matching the specified day of the week.
     *
     * @param targetDayOfWeek The target day of the week (e.g., "MONDAY").
     * @param leadEventList   The list of events to filter.
     * @return A filtered list of events that match the target day of the week.
     */
    default List<Event> filterByTargetedDayOfWeek(String targetDayOfWeek, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> event.getDayOfWeek().equalsIgnoreCase(targetDayOfWeek))
                .collect(Collectors.toList());
    }


    /**
     * Filters a list of events to include only those matching the specified period of the day.
     *
     * @param targetPeriodOfDay The target period of the day (MORNING, AFTERNOON, or EVENING).
     * @param leadEventList     The list of events to filter.
     * @return A filtered list of events that match the target period of the day.
     */
    default List<Event> filterByTargetedPeriodOfDay(String targetPeriodOfDay, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> event.getPeriodOfDay().equalsIgnoreCase(targetPeriodOfDay))
                .collect(Collectors.toList());
    }


    /**
     * Filters a list of events to exclude those matching the specified period of the day.
     *
     * @param previousPeriodOfDay The period of the day to exclude (MORNING, AFTERNOON, or EVENING).
     * @param leadEventList       The list of events to filter.
     * @return A filtered list of events that do not match the specified period of the day.
     */
    default List<Event> filterByFallbackPeriodOfDay(PeriodOfDay previousPeriodOfDay, List<Event> leadEventList) {
        return leadEventList.stream()
                .filter(event -> !event.getPeriodOfDay().equalsIgnoreCase(previousPeriodOfDay.toString()))
                .collect(Collectors.toList());
    }


    /**
     * Deviates the given time by a random number of minutes within a specified maximum deviation in hours.
     *
     * @param originalTime      The original time to deviate.
     * @param maxDeviationHours The maximum deviation in hours (converted to minutes).
     * @return The deviated time as a LocalTime.
     */
    default LocalTime deviateByMinutes(LocalTime originalTime, int maxDeviationHours) {
        int maxDeviationMinutes = maxDeviationHours * 60; // Convert hours to minutes
        int randomDeviation = (int) (Math.random() * (2 * maxDeviationMinutes + 1)) - maxDeviationMinutes;
        return originalTime.plusMinutes(randomDeviation);
    }


    /**
     * Finds the event time closest to the specified target time.
     *
     * @param eventList  The list of events to evaluate.
     * @param targetTime The target time to find the closest match.
     * @return The time of the event closest to the target time, or the target time if no events exist.
     */
    default LocalTime findClosestEventTime(List<Event> eventList, LocalTime targetTime) {
        return eventList.stream()
                .map(event -> event.getCreatedAt().toLocalTime()) // Convert createdAt to LocalTime
                .min(Comparator.comparingInt(time -> Math.toIntExact(Math.abs(Duration.between(time, targetTime).toMinutes()))))
                .orElse(targetTime); // Fallback to targetTime if no events are present
    }


}
