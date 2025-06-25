package com.saleset.integration.zoho.dto.response;

import org.json.JSONArray;
import org.json.JSONObject;

public class ZohoLeadUpsertResponse {

    private final String responseCode;
    private final String zohoLeadId;

    /**
     * Parses the raw JSON response from a Zoho CRM upsert (create/update) call,
     * extracting both the operation status code and the resulting record ID.
     * <p>
     * Handles three payload shapes:
     * <ul>
     *   <li>A standard `data` array containing an entry with `code` and `details.id`</li>
     *   <li>A duplicate‐data case where the ID lives under `details.duplicate_record.id`</li>
     *   <li>A direct‐error object (no `data` array) with top‐level `code` and `details` fields</li>
     * </ul>
     *
     * @param responseBody the full JSON response string returned by Zoho CRM
     */
    public ZohoLeadUpsertResponse(String responseBody) {
        JSONObject root = new JSONObject(responseBody);
        JSONArray data = root.optJSONArray("data");

        // Try to read top-level code (error payloads) or fallback to array entry code
        String code = root.optString("code", null);
        String id   = null;

        if (data != null && !data.isEmpty()) {
            // Standard array-based response
            JSONObject entry = data.getJSONObject(0);
            code = entry.optString("code", code);
            JSONObject details = entry.optJSONObject("details");
            if (details != null) {
                // success path: details.id
                id = details.optString("id", null);
                // duplicate path: details.duplicate_record.id
                if (id == null) {
                    JSONObject duplicateRecord = details.optJSONObject("duplicate_record");
                    if (duplicateRecord != null) {
                        id = duplicateRecord.optString("id", null);
                    }
                }
            }
        } else {
            // Fallback: direct error object
            JSONObject details = root.optJSONObject("details");
            if (details != null) {
                // direct id
                id = details.optString("id", null);
                if (id == null) {
                    JSONObject duplicateRecord = details.optJSONObject("duplicate_record");
                    if (duplicateRecord != null) {
                        id = duplicateRecord.optString("id", null);
                    }
                }
            }
        }

        this.responseCode = code;
        this.zohoLeadId   = id;
    }

    public String getZohoLeadId() { return zohoLeadId; }

    public String getResponseCode() { return responseCode; }

    public boolean isDuplicate() { return this.responseCode.equals("DUPLICATE_DATA"); }

    public boolean isInvalidData() { return this.responseCode.equals("INVALID_DATA"); }

    @Override
    public String toString() {
        return "ZohoLeadResponse{" +
                "responseCode='" + responseCode + '\'' +
                ", zohoLeadId='" + zohoLeadId + '\'' +
                '}';
    }

}
