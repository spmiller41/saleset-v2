package com.saleset.core.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public class AppointmentRequest {

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

    @JsonProperty("bookingSource")
    private String bookingSource;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("street")
    private String street;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zip")
    private String zip;

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

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getStreet() { return street; }

    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }

    public void setZip(String zip) { this.zip = zip; }

    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", appointmentType='" + appointmentType + '\'' +
                ", leadBookingUUID='" + leadBookingUUID + '\'' +
                ", bookingReference='" + bookingReference + '\'' +
                ", bookingSource='" + bookingSource + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }

}
