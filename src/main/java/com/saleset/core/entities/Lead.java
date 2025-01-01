package com.saleset.core.entities;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // FOREIGN KEY
    @Column(name = "contact_id")
    private int contactId;

    // FOREIGN KEY
    @Column(name = "address_id")
    private int addressId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "next_follow_up")
    private LocalDateTime nextFollowUp;

    @Column(name = "previous_follow_up")
    private LocalDateTime previousFollowUp;

    @Column(name = "stage")
    private String stage;

    @Column(name = "stage_updated_at")
    private LocalDateTime stageUpdatedAt;

    @Column(name = "booking_page_url")
    private String bookingPageUrl;

    @Column(name = "lead_source")
    private String leadSource;

    @Column(name = "sub_source")
    private String subSource;

    @Column(name = "external_id")
    private String externalId;

    public Lead() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getNextFollowUp() {
        return nextFollowUp;
    }

    public void setNextFollowUp(LocalDateTime nextFollowUp) {
        this.nextFollowUp = nextFollowUp;
    }

    public LocalDateTime getPreviousFollowUp() {
        return previousFollowUp;
    }

    public void setPreviousFollowUp(LocalDateTime previousFollowUp) {
        this.previousFollowUp = previousFollowUp;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public LocalDateTime getStageUpdatedAt() {
        return stageUpdatedAt;
    }

    public void setStageUpdatedAt(LocalDateTime stageUpdatedAt) {
        this.stageUpdatedAt = stageUpdatedAt;
    }

    public String getBookingPageUrl() {
        return bookingPageUrl;
    }

    public void setBookingPageUrl(String bookingPageUrl) {
        this.bookingPageUrl = bookingPageUrl;
    }

    public String getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "Lead{" +
                "id=" + id +
                ", contactId=" + contactId +
                ", addressId=" + addressId +
                ", uuid='" + uuid + '\'' +
                ", createdAt=" + createdAt +
                ", nextFollowUp=" + nextFollowUp +
                ", previousFollowUp=" + previousFollowUp +
                ", stage='" + stage + '\'' +
                ", stageUpdatedAt=" + stageUpdatedAt +
                ", bookingPageUrl='" + bookingPageUrl + '\'' +
                ", leadSource='" + leadSource + '\'' +
                ", subSource='" + subSource + '\'' +
                ", externalId='" + externalId + '\'' +
                '}';
    }

}
