package com.saleset.core.service.outreach.templates;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TemplateUtil {

    /**
     * Retrieves the current day of the week with the first letter capitalized.
     * @return A String representing the current day of the week.
     */
    public static String getDayOfWeek() {
        String dayOfWeek = LocalDateTime.now().getDayOfWeek().toString();
        return dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
    }


    /**
     * Determines the current period of the day based on the current time.
     * @return A String representing the current period of the day.
     */
    public static String getTimePeriodOfDay() {
        LocalTime now = LocalTime.now();

        // Define the time thresholds
        LocalTime afternoonStart = LocalTime.NOON;
        LocalTime eveningStart = LocalTime.of(17, 0); // 5 PM

        // Compare the current time with the thresholds
        if (now.isBefore(afternoonStart)) {
            return "morning";
        } else if (now.isBefore(eveningStart)) {
            return "afternoon";
        } else {
            return "evening";
        }
    }

}
