package com.saleset.core.service.outreach.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaskConfig {

    @Value("${task.followUpWindowMinutes}")
    private int followUpWindowMinutes;

    public int getFollowUpWindowMinutes() { return followUpWindowMinutes; }

    public long getPollingIntervalMillis() { return followUpWindowMinutes * 60 * 1000L; }

}
