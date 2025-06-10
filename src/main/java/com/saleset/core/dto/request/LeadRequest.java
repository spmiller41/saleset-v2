package com.saleset.core.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.saleset.core.enums.PhoneLineType;

/**
 * Represents the data transfer object (DTO) for lead information.
 * This class is used to deserialize JSON payloads into Java objects
 * when receiving data via POST requests.
 * <p>
 * Fields required for deserialization:
 * - {@code first_name}: First name of the lead.<p>
 * - {@code last_name}: Last name of the lead.<p>
 * - {@code email}: Email address of the lead.<p>
 * - {@code primary_phone}: Primary phone number.<p>
 * - {@code secondary_phone}: Secondary phone number.<p>
 * - {@code street}: Street address of the lead.<p>
 * - {@code unit}: Unit or apartment number.<p>
 * - {@code city}: City of the lead's address.<p>
 * - {@code state}: State of the lead's address.<p>
 * - {@code zip_code}: ZIP or postal code of the address.<p>
 * - {@code lead_source}: Source from which the lead originated.<p>
 * - {@code sub_source}: Sub-source for finer lead tracking.<p>
 * - {@code zcrm_external_id}: External ID for uniquely identifying leads across systems.<p>
 * - {@code zcrm_auto_number}: Secondary external id for uniquely identifying leads across modules.<p>
 * - {@code stage}: Stage of the current lead <p>
 * - Note: The stage of the lead MUST include 'New', 'Retargeted_No_Show', or 'Retargeted_Rehash'.
 */
public class LeadRequest {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("primary_phone")
    private String primaryPhone;

    @JsonProperty("secondary_phone")
    private String secondaryPhone;

    @JsonProperty("street")
    private String street;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("lead_source")
    private String leadSource;

    @JsonProperty("sub_source")
    private String subSource;

    @JsonProperty("zcrm_external_id")
    private String zcrmExternalId;

    @JsonProperty("zcrm_auto_number")
    private String zcrmAutoNumber;

    @JsonProperty("stage")
    private String stage;

    private PhoneLineType primaryPhoneType;

    private PhoneLineType secondaryPhoneType;

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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public String getZcrmExternalId() {
        return zcrmExternalId;
    }

    public void setZcrmExternalId(String zcrmExternalId) {
        this.zcrmExternalId = zcrmExternalId;
    }

    public String getZcrmAutoNumber() { return zcrmAutoNumber; }

    public void setZcrmAutoNumber(String zcrmAutoNumber) { this.zcrmAutoNumber = zcrmAutoNumber; }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public PhoneLineType getPrimaryPhoneType() {
        return primaryPhoneType;
    }

    public void setPrimaryPhoneType(PhoneLineType primaryPhoneType) {
        this.primaryPhoneType = primaryPhoneType;
    }

    public PhoneLineType getSecondaryPhoneType() {
        return secondaryPhoneType;
    }

    public void setSecondaryPhoneType(PhoneLineType secondaryPhoneType) { this.secondaryPhoneType = secondaryPhoneType; }

    @Override
    public String toString() {
        return "LeadRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", primaryPhone='" + primaryPhone + '\'' +
                ", secondaryPhone='" + secondaryPhone + '\'' +
                ", street='" + street + '\'' +
                ", unit='" + unit + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", leadSource='" + leadSource + '\'' +
                ", subSource='" + subSource + '\'' +
                ", zcrmExternalId='" + zcrmExternalId + '\'' +
                ", zcrmAutoNumber='" + zcrmAutoNumber + '\'' +
                ", stage='" + stage + '\'' +
                ", primaryPhoneType=" + primaryPhoneType +
                ", secondaryPhoneType=" + secondaryPhoneType +
                '}';
    }

}
