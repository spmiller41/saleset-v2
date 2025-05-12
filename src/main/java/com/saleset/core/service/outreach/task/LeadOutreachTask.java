package com.saleset.core.service.outreach.task;

import com.saleset.core.entities.Lead;
import com.saleset.core.service.outreach.Dispatcher;
import com.saleset.core.service.transaction.leads.LeadEngagementManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeadOutreachTask {

    private final Dispatcher dispatcher;
    private final LeadEngagementManager engagementManager;
    private final TaskConfig taskConfig;
    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    @Autowired
    public LeadOutreachTask(Dispatcher dispatcher, LeadEngagementManager engagementManager, TaskConfig taskConfig) {
        this.dispatcher = dispatcher;
        this.engagementManager = engagementManager;
        this.taskConfig = taskConfig;
    }

    @PostConstruct
    public void init() {
        scheduler.initialize();
        scheduler.scheduleAtFixedRate(this::runOutreachTask, Duration.ofMillis(taskConfig.getPollingIntervalMillis()));
    }

    private void runOutreachTask() {
        System.out.println("Task Executed: " + LocalDateTime.now());

        List<Lead> leadList = engagementManager.scanForFollowUpLeads(taskConfig.getFollowUpWindowMinutes());
        leadList.forEach(dispatcher::executeSmsFollowUp);
    }

}
