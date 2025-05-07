package com.saleset.core.enums;

public enum EventSource {

    SMS("Sms"),
    EMAIL("Email");

    private final String eventSource;

    EventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    @Override
    public String toString() { return eventSource; }

}
