package com.saleset.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public class AppointmentDataTransfer {

    @JsonProperty("startsAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDateTime;

    @JsonProperty("endsAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDateTime;

    @JsonProperty("appointmentType")
    private String appointmentType;

    @JsonProperty("uuid")
    private String leadBookingUUID;

    @JsonProperty("bookingReference")
    private String bookingReference;

    // Note: May want to make this non-null when ready.
    @JsonProperty("bookingSource")
    private String bookingSource;

    public LocalDateTime getStartDateTime() { return startDateTime; }

    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }

    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public String getAppointmentType() { return appointmentType; }

    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }

    public String getLeadBookingUUID() { return leadBookingUUID; }

    public void setLeadBookingUUID(String leadBookingUUID) { this.leadBookingUUID = leadBookingUUID; }

    public String getBookingReference() { return bookingReference; }

    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getBookingSource() { return bookingSource; }

    public void setBookingSource(String bookingSource) { this.bookingSource = bookingSource; }

    @Override
    public String toString() {
        return "AppointmentDataTransfer{" +
                "startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", appointmentType='" + appointmentType + '\'' +
                ", leadBookingUUID='" + leadBookingUUID + '\'' +
                ", bookingReference='" + bookingReference + '\'' +
                ", bookingSource='" + bookingSource + '\'' +
                '}';
    }

}
