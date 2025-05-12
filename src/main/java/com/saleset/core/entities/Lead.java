package com.saleset.core.entities;

import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.enums.LeadStage;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private Integer addressId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "follow_up_count")
    private int followUpCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "next_follow_up")
    private LocalDateTime nextFollowUp;

    @Column(name = "previous_follow_up")
    private LocalDateTime previousFollowUp;

    @Column(name = "original_stage")
    private String originalStage;

    @Column(name = "current_stage")
    private String currentStage;

    @Column(name = "stage_updated_at")
    private LocalDateTime stageUpdatedAt;

    @Column(name = "booking_page_url")
    private String bookingPageUrl;

    @Column(name = "tracking_webhook_url")
    private String trackingWebhookUrl;

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

    public Lead(LeadDataTransfer leadData, Contact contact, Address address, LocalDateTime scheduledOutreach) {
        setContactId(contact.getId());
        setAddressId(address.getId());
        setUuid(UUID.randomUUID().toString());
        setFollowUpCount(0);
        setCreatedAt(LocalDateTime.now());
        setOriginalStage(leadData.getStage());
        setCurrentStage(leadData.getStage());
        setStageUpdatedAt(LocalDateTime.now());
        setNextFollowUp(scheduledOutreach);
        setPreviousFollowUp(scheduledOutreach);
        setLeadSource(leadData.getLeadSource());
        setSubSource(leadData.getSubSource());
        setExternalId(leadData.getExternalId());
    }

    public Lead(LeadDataTransfer leadData, Contact contact, LocalDateTime scheduledOutreach) {
        setContactId(contact.getId());
        setUuid(UUID.randomUUID().toString());
        setFollowUpCount(0);
        setCreatedAt(LocalDateTime.now());
        setOriginalStage(leadData.getStage());
        setCurrentStage(leadData.getStage());
        setStageUpdatedAt(LocalDateTime.now());
        setNextFollowUp(scheduledOutreach);
        setPreviousFollowUp(scheduledOutreach);
        setLeadSource(leadData.getLeadSource());
        setSubSource(leadData.getSubSource());
        setExternalId(leadData.getExternalId());
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

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getFollowUpCount() { return followUpCount; }

    public void setFollowUpCount(int followUpCount) { this.followUpCount = followUpCount; }

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

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
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

    public String getOriginalStage() { return originalStage; }

    public void setOriginalStage(String originalStage) { this.originalStage = originalStage; }

    public String getTrackingWebhookUrl() { return trackingWebhookUrl; }

    public void setTrackingWebhookUrl(String trackingWebhookUrl) { this.trackingWebhookUrl = trackingWebhookUrl; }

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
                ", originalStage='" + originalStage + '\'' +
                ", currentStage='" + currentStage + '\'' +
                ", stageUpdatedAt=" + stageUpdatedAt +
                ", bookingPageUrl='" + bookingPageUrl + '\'' +
                ", trackingWebhookUrl='" + trackingWebhookUrl + '\'' +
                ", leadSource='" + leadSource + '\'' +
                ", subSource='" + subSource + '\'' +
                ", externalId='" + externalId + '\'' +
                '}';
    }

}
