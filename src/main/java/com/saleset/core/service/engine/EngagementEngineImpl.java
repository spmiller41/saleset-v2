package com.saleset.core.service.engine;

import com.saleset.core.entities.Event;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.enums.PeriodOfDay;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implementation of the EngagementEngine interface that provides core functionality
 * for determining follow-up times, follow-up dates, and lead stage transitions.
 * <p>
 * This class is responsible for applying business logic to analyze lead interactions,
 * determine the optimal time for follow-ups, and adjust lead stages based on elapsed time.
 * <p>
 * Key Features:
 * - Calculates the next follow-up time by analyzing lead interactions and applying fallback logic.
 * - Determines follow-up dates based on time elapsed since the last follow-up and a dynamic divisor.
 * - Evaluates lead stages to ensure proper progression based on predefined thresholds.
 * <p>
 * This implementation also ensures that follow-ups occur only during acceptable hours (8:00 AM to 8:00 PM),
 * with random deviations for a more humanized interaction approach.
 * <p>
 * Annotated with @Service to integrate with Spring's dependency injection framework.
 */
@Service
public class EngagementEngineImpl implements EngagementEngine {


    /**
     * Determines the follow-up time for a lead based on previous interactions, target day,
     * and target period of day. If no relevant interactions exist, falls back to peak hours
     * and adjusts the time within acceptable messaging hours.
     *
     * @param previousFollowUp the time of the previous follow-up
     * @param targetDate the date for the next follow-up
     * @param leadEventList the list of events associated with the lead
     * @return the LocalTime for the next follow-up
     */
    @Override
    public LocalTime determineFollowUpTime(LocalDateTime previousFollowUp, LocalDate targetDate, List<Event> leadEventList) {
        String targetDayOfWeek = determineTargetDayOfWeek(targetDate);
        PeriodOfDay targetPeriodOfDay = determineTargetPeriodOfDay(previousFollowUp.toLocalTime());

        List<Event> eventList = filterByTargetedDayOfWeek(targetDayOfWeek, leadEventList);
        if (eventList.isEmpty()) {
            eventList = filterByTimePeriod(7, leadEventList);
            if (eventList.isEmpty()) {
                LocalTime peakHour = determinePeakHour(targetPeriodOfDay);
                return deviateByMinutes(peakHour, 2);
            }
        }

        List<Event> filteredEventList = filterByTargetedPeriodOfDay(targetPeriodOfDay.toString(), eventList);
        if (filteredEventList.isEmpty()) {
            PeriodOfDay previousPeriodOfDay = determinePeriodOfDay(previousFollowUp.toLocalTime());
            filteredEventList = filterByFallbackPeriodOfDay(previousPeriodOfDay, eventList);
            if (filteredEventList.isEmpty()) {
                LocalTime peakHour = determinePeakHour(previousPeriodOfDay);
                return generateDeterminedTime(filteredEventList, peakHour);
            } else {
                targetPeriodOfDay = determinePeriodOfDay(filteredEventList.get(0).getCreatedAt().toLocalTime());
                LocalTime peakHour = determinePeakHour(targetPeriodOfDay);
                return generateDeterminedTime(filteredEventList, peakHour);
            }
        }

        LocalTime peakHour = determinePeakHour(targetPeriodOfDay);
        return generateDeterminedTime(filteredEventList, peakHour);
    }


    /**
     * Determines the next follow-up date based on the time since the last follow-up
     * and a divisor that adjusts the frequency of follow-ups.
     *
     * @param fromDate the date and time of the last follow-up
     * @param follow_up_divisor the divisor to calculate follow-up frequency
     * @return the LocalDate for the next follow-up
     */
    @Override
    public LocalDate determineFollowUpDate(LocalDateTime fromDate, double follow_up_divisor) {
        long daysFromLastFollowUp = Math.abs(ChronoUnit.DAYS.between(fromDate.toLocalDate(), LocalDate.now()));
        long daysUntilFollowUp = (long) (daysFromLastFollowUp / follow_up_divisor);
        if (daysUntilFollowUp == 0) daysUntilFollowUp = 1;
        return LocalDate.now().plusDays(daysUntilFollowUp);
    }


    /**
     * Determines the next stage of the lead based on the time elapsed since its creation.
     * If the lead is older than a specified maximum number of days in the current stage,
     * it transitions to the next stage.
     *
     * @param fromDate the date and time the lead was created
     * @param maxDaysInStage the maximum number of days a lead can stay in the current stage
     * @return the LeadStage indicating the current or next stage of the lead
     */
    @Override
    public LeadStage determineNextStage(LocalDateTime fromDate, int maxDaysInStage) {
        long daysSinceCreation = ChronoUnit.DAYS.between(fromDate.toLocalDate(), LocalDate.now());
        return daysSinceCreation >= maxDaysInStage ? LeadStage.AGED_LOW_PRIORITY : LeadStage.NEW;
    }


    private LocalTime generateDeterminedTime(List<Event> events, LocalTime peakHour) {
        LocalTime determinedTime = findClosestEventTime(events, peakHour);
        LocalTime earliestTime = LocalTime.of(8, 0); // 8:00am
        LocalTime latestTime = LocalTime.of(20, 0); // 8:00pm

        if (determinedTime.isBefore(earliestTime) || determinedTime.isAfter(latestTime)) {
            determinedTime = deviateByMinutes(peakHour, 2);
        } else {
            determinedTime = deviateByMinutes(determinedTime, 1);
        }

        return determinedTime;
    }


}
