package com.saleset.core.entities;

import com.saleset.core.dto.AppointmentRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "starts_at")
    private LocalDateTime startDateTime;

    @Column(name = "ends_at")
    private LocalDateTime endDateTime;

    @Column(name = "booking_source")
    private String bookingSource;

    @Column(name = "appointment_type")
    private String appointmentType;

    @Column(name = "booking_reference")
    private String bookingReference;

    // FOREIGN KEY
    @Column(name = "lead_id")
    private int leadId;

    public Appointment() {}

    public Appointment(AppointmentRequest appointmentData, Lead lead) {
        setStartDateTime(appointmentData.getStartDateTime());
        setEndDateTime(appointmentData.getEndDateTime());
        setBookingSource(appointmentData.getBookingSource());
        setAppointmentType(appointmentData.getAppointmentType());
        setBookingReference(appointmentData.getBookingReference());
        setLeadId(lead.getId());
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public LocalDateTime getStartDateTime() { return startDateTime; }

    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }

    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public String getBookingSource() { return bookingSource; }

    public void setBookingSource(String bookingSource) { this.bookingSource = bookingSource; }

    public String getAppointmentType() { return appointmentType; }

    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }

    public String getBookingReference() { return bookingReference; }

    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public int getLeadId() { return leadId; }

    public void setLeadId(int leadId) { this.leadId = leadId; }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", bookingSource='" + bookingSource + '\'' +
                ", appointmentType='" + appointmentType + '\'' +
                ", bookingReference='" + bookingReference + '\'' +
                ", leadId=" + leadId +
                '}';
    }

}
