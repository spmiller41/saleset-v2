package com.saleset.core.entities;

import com.saleset.core.dto.EventDataTransfer;
import com.saleset.core.enums.EventSource;
import com.saleset.core.enums.EventType;
import com.saleset.core.util.TimePeriodIdentifier;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Column(name = "source")
    private String source;

    public Event() {}

    public Event(Lead lead, EventDataTransfer eventData, EventSource source) {

        if (eventData.getEvent().equalsIgnoreCase(EventType.OPEN.toString())) {
            setEventType(EventType.OPEN.toString());
        } else if (eventData.getEvent().equalsIgnoreCase(EventType.CLICK.toString())) {
            setEventType(EventType.CLICK.toString());
        }

        setLeadId(lead.getId());
        setCreatedAt(LocalDateTime.now());
        setDayOfWeek(LocalDate.now().getDayOfWeek().toString());
        setPeriodOfDay(TimePeriodIdentifier.identifyPeriodOfDay(LocalTime.now()).toString());
        setSource(source.toString());

    }

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

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", leadId=" + leadId +
                ", eventType='" + eventType + '\'' +
                ", createdAt=" + createdAt +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", periodOfDay='" + periodOfDay + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

}
