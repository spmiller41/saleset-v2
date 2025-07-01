package com.saleset.core.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatting {

    public static String toFormattedDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MMMM d"));
    }

    public static String toFormattedTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    public static String toFormattedDate(String timestamp) {
        // Parse the ISO 8601 formatted string to OffsetDateTime
        OffsetDateTime odt = OffsetDateTime.parse(timestamp);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        return odt.format(dateFormatter);
    }

    public static String toFormattedTime(String timestamp) {
        // Parse the ISO 8601 formatted string to OffsetDateTime
        OffsetDateTime odt = OffsetDateTime.parse(timestamp);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        return odt.format(timeFormatter);
    }

    public static LocalDateTime toLocalDateTime(String timestamp) {
        // Parse the ISO 8601 formatted string to OffsetDateTime
        OffsetDateTime odt = OffsetDateTime.parse(timestamp);

        // Convert OffsetDateTime to LocalDateTime by ignoring the offset
        return odt.toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(String timestamp, int hoursToAdd) {
        // Parse the ISO 8601 formatted string to OffsetDateTime
        OffsetDateTime odt = OffsetDateTime.parse(timestamp);

        // Add hours to the OffsetDateTime
        OffsetDateTime adjustedOdt = odt.plusHours(hoursToAdd);

        // Convert OffsetDateTime to LocalDateTime by ignoring the offset
        return adjustedOdt.toLocalDateTime();
    }

}
