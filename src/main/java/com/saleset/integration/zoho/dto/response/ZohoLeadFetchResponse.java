package com.saleset.integration.zoho.dto.response;

import org.json.JSONObject;

public class ZohoLeadFetchResponse {

    private final String id;
    private final String autoNumber;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String mobile;
    private final String email;
    private final String street;
    private final String state;
    private final String city;
    private final String zip;

    public ZohoLeadFetchResponse(String responseBody) {
        JSONObject entry = new JSONObject(responseBody)
                .optJSONArray("data")
                .optJSONObject(0);

        id         = entry != null ? entry.optString("id"           , null) : null;
        autoNumber = entry != null ? entry.optString("Auto_Number_1", null) : null;
        firstName  = entry != null ? entry.optString("First_Name"   , null) : null;
        lastName   = entry != null ? entry.optString("Last_Name"    , null) : null;
        phone      = entry != null ? entry.optString("Phone"        , null) : null;
        mobile     = entry != null ? entry.optString("Mobile"       , null) : null;
        email      = entry != null ? entry.optString("Email"        , null) : null;
        street     = entry != null ? entry.optString("Street"       , null) : null;
        city       = entry != null ? entry.optString("City"         , null) : null;
        state      = entry != null ? entry.optString("State"        , null) : null;
        zip        = entry != null ? entry.optString("Zip_Code"     , null) : null;
    }

    public String getId() { return id; }
    public String getAutoNumber() { return autoNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getMobile() { return mobile; }
    public String getEmail() { return email; }
    public String getStreet() { return street; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public String getZip() { return zip; }

    @Override
    public String toString() {
        return "ZohoLeadFetchResponse{" +
                "id='" + id + '\'' +
                ", autoNumber='" + autoNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", street='" + street + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }

}
