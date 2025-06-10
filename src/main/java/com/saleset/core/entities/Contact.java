package com.saleset.core.entities;

import com.saleset.core.dto.LeadRequest;
import jakarta.persistence.*;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "primary_phone")
    private String primaryPhone;

    @Column(name = "primary_phone_type")
    private String primaryPhoneType;

    @Column(name = "secondary_phone")
    private String secondaryPhone;

    @Column(name = "secondary_phone_type")
    private String secondaryPhoneType;

    public Contact() {}

    public Contact(LeadRequest leadData) {
        setFirstName(leadData.getFirstName());
        setLastName(leadData.getLastName());
        setEmail(leadData.getEmail());
        setPrimaryPhone(leadData.getPrimaryPhone());
        setPrimaryPhoneType(leadData.getPrimaryPhoneType().toString());

        // Only set Secondary Phone and Type if the phone exists
        if (leadData.getSecondaryPhone() != null) {
            setSecondaryPhone(leadData.getSecondaryPhone());
            setSecondaryPhoneType(leadData.getSecondaryPhoneType() != null
                    ? leadData.getSecondaryPhoneType().toString()
                    : null);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public String getPrimaryPhoneType() { return primaryPhoneType; }

    public void setPrimaryPhoneType(String primaryPhoneType) { this.primaryPhoneType = primaryPhoneType; }

    public String getSecondaryPhoneType() { return secondaryPhoneType; }

    public void setSecondaryPhoneType(String secondaryPhoneType) { this.secondaryPhoneType = secondaryPhoneType; }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", primaryPhone='" + primaryPhone + '\'' +
                ", primaryPhoneType='" + primaryPhoneType + '\'' +
                ", secondaryPhone='" + secondaryPhone + '\'' +
                ", secondaryPhoneType='" + secondaryPhoneType + '\'' +
                '}';
    }

}
