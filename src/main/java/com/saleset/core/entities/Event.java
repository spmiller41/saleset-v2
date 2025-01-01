package com.saleset.core.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // FOREIGN KEY
    @Column(name = "lead_id")
    private int leadId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "period_of_day")
    private String periodOfDay;

    public Event() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getPeriodOfDay() {
        return periodOfDay;
    }

    public void setPeriodOfDay(String periodOfDay) {
        this.periodOfDay = periodOfDay;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", leadId=" + leadId +
                ", eventType='" + eventType + '\'' +
                ", createdAt=" + createdAt +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", periodOfDay='" + periodOfDay + '\'' +
                '}';
    }

}
