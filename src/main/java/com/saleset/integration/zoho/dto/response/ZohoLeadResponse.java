package com.saleset.integration.zoho.dto.response;

import org.json.JSONArray;
import org.json.JSONObject;

public class ZohoLeadResponse {

    private final String responseCode;
    private final String zohoLeadId;

    public ZohoLeadResponse(String responseBody) {
        JSONObject root = new JSONObject(responseBody);
        JSONArray data = root.optJSONArray("data");

        String code = null;
        String id   = null;

        if (data != null && !data.isEmpty()) {
            JSONObject entry   = data.getJSONObject(0);
            code = entry.optString("code", null);

            JSONObject details = entry.optJSONObject("details");
            if (details != null) {
                // success path: details.id
                id = details.optString("id", null);
                // duplicate path: details.duplicate_record.id
                if (id == null) {
                    JSONObject dup = details.optJSONObject("duplicate_record");
                    if (dup != null) {
                        id = dup.optString("id", null);
                    }
                }
            }
        }

        this.responseCode = code;
        this.zohoLeadId   = id;
    }


    public String getResponseCode() { return responseCode; }

    public String getZohoLeadId() { return zohoLeadId; }

    @Override
    public String toString() {
        return "ZohoLeadResponse{" +
                "responseCode='" + responseCode + '\'' +
                ", zohoLeadId='" + zohoLeadId + '\'' +
                '}';
    }

}
