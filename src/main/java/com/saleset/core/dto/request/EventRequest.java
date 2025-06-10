package com.saleset.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventRequest {

    @JsonProperty("event")
    private String event;

    @JsonProperty("lead_uuid")
    private String leadUUID;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getLeadUUID() {
        return leadUUID;
    }

    public void setLeadUUID(String leadUUID) {
        this.leadUUID = leadUUID;
    }

    @Override
    public String toString() {
        return "EventRequest{" +
                "event='" + event + '\'' +
                ", leadUUID='" + leadUUID + '\'' +
                '}';
    }

}
