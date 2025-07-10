package com.saleset.usecase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ZohoAppointmentRequest {

    @JsonProperty("First_Name")
    private String firstName;

    @JsonProperty("Last_Name")
    private String lastName;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("City")
    private String city;

    @JsonProperty("State")
    private String state;

    @JsonProperty("Zip_Code")
    private String zipCode;

    private LocalDateTime appointmentDateTime;

    public ZohoAppointmentRequest() {}

    // Jackson will call this with the full ISO timestamp (with offset)
    @JsonProperty("Appointment")
    public void setAppointment(String isoOffsetTimestamp) {
        // parse "2025-07-14T10:00:00-04:00" → LocalDateTime without the offset
        this.appointmentDateTime = OffsetDateTime
                .parse(isoOffsetTimestamp)
                .toLocalDateTime();
    }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }

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

    public String getZipCode() { return zipCode; }

    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    @Override
    public String toString() {
        return "ZohoAppointmentRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", appointmentDateTime=" + appointmentDateTime +
                '}';
    }

}
