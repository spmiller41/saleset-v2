package com.saleset;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class TimeZoneConfig {
    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    }
}
