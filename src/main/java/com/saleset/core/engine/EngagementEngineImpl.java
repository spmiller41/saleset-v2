package com.saleset.core.engine;

import com.saleset.core.entities.Event;
import com.saleset.core.enums.PeriodOfDay;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class EngagementEngineImpl implements EngagementEngine {
    @Override
    public LocalTime determineFollowUpTime(LocalDateTime previousFollowUp, LocalDate targetDate, List<Event> leadEventList) {
        String targetDayOfWeek = determineTargetDayOfWeek(targetDate);
        System.out.println("Target Day of Week: " + targetDayOfWeek);
        PeriodOfDay targetPeriodOfDay = determineTargetPeriodOfDay(previousFollowUp.toLocalTime());
        System.out.println("Target Period-Of-Day: " + targetPeriodOfDay);

        List<Event> eventList = filterByTargetedDayOfWeek(targetDayOfWeek, leadEventList);
        System.out.println("List of events filtered to the targeted day of week: " + eventList);
        if (eventList.isEmpty()) {
            eventList = filterByTimePeriod(7, leadEventList);
            System.out.println("No events for targeted day of week. Fall back to all events in the last 7 days: " + eventList);
            if (eventList.isEmpty()) {
                LocalTime peakHour = determinePeakHour(targetPeriodOfDay);
                System.out.println("Peak Hour: " + peakHour);
                LocalTime determinedTime = deviateByMinutes(peakHour, 2);
                System.out.println("No events in the last 7 days, default time determined from target period of day: " + determinedTime);
                return determinedTime;
            }
        }

        List<Event> filteredEventList = filterByTargetedPeriodOfDay(targetPeriodOfDay.toString(), eventList);
        System.out.println("Filtered events including target period of day: " + filteredEventList);
        if (filteredEventList.isEmpty()) {
            System.out.println("No events on this day with targeted period of day.");
            PeriodOfDay previousPeriodOfDay = determinePeriodOfDay(previousFollowUp.toLocalTime());
            System.out.println("Previous period of day used: " + previousPeriodOfDay);
            filteredEventList = filterByFallbackPeriodOfDay(previousPeriodOfDay, eventList);
            System.out.println("Events with fallback period of day: " + filteredEventList);
            if (filteredEventList.isEmpty()) {
                System.out.println("No events matching the fallback period of day used.");
                LocalTime peakHour = determinePeakHour(previousPeriodOfDay);
                System.out.println("Using previous period of day as fallback: " + previousPeriodOfDay);
                System.out.println("Peak Hour: " + peakHour);
                LocalTime determinedTime = findClosestEventTime(eventList, peakHour);
                determinedTime = deviateByMinutes(determinedTime, 1);
                System.out.println("Determined time: " + determinedTime);
                return determinedTime;
            } else {
                targetPeriodOfDay = determinePeriodOfDay(filteredEventList.get(0).getCreatedAt().toLocalTime());
                System.out.println("We have a period of day that was not used in last follow up: " + targetPeriodOfDay);
                LocalTime peakHour = determinePeakHour(targetPeriodOfDay);
                System.out.println("Peak Hour: " + peakHour);
                LocalTime determinedTime = findClosestEventTime(filteredEventList, peakHour);
                determinedTime = deviateByMinutes(determinedTime, 1);
                System.out.println("Determined time: " + determinedTime);
                return determinedTime;
            }
        } else {
            System.out.println("We have an event for the targeted period of day: " + targetPeriodOfDay);
            LocalTime peakHour = determinePeakHour(targetPeriodOfDay);
            System.out.println("Peak Hour: " + peakHour);
            LocalTime determinedTime = findClosestEventTime(filteredEventList, peakHour);
            determinedTime = deviateByMinutes(determinedTime, 1);
            System.out.println("Determined time: " + determinedTime);
            return determinedTime;
        }
    }

    @Override
    public LocalDate determineFollowUpDate(LocalDateTime fromDate, double follow_up_divisor) {
        return null;
    }

    @Override
    public String determineNextStage(LocalDateTime fromDate, int maxDaysInStage) {
        return "";
    }
}
