package com.saleset.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "in_market_zips")
public class MarketZipData {

    @Id
    @Column(name = "zip")
    private String zipCode;

    @Column(name = "county")
    private String county;

    @Column(name = "appointment_type")
    private String appointmentType;

    public MarketZipData() {}

    public String getZipCode() { return zipCode; }
    public String getCounty() { return county; }
    public String getAppointmentType() { return appointmentType; }

    @Override
    public String toString() {
        return "MarketZipData{" +
                "zipCode='" + zipCode + '\'' +
                ", county='" + county + '\'' +
                ", appointmentType='" + appointmentType + '\'' +
                '}';
    }

}
