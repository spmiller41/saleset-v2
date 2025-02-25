package com.saleset.core.enums;

public enum EventType {

    CLICK("Click"),
    OPEN("Open");

    private final String eventType;

    EventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() { return eventType; }

}
