package com.saleset.core.service.outreach.task;

import com.saleset.core.entities.Lead;
import com.saleset.core.service.outreach.Dispatcher;
import com.saleset.core.service.transaction.leads.LeadEngagementManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LeadOutreachTask is responsible for scheduling and executing follow-up communication tasks
 * for leads that are due for outreach. It runs on a fixed interval, scanning for leads ready
 * for contact and dispatching SMS follow-ups while updating their engagement state.
 * <p>
 * This service initializes a scheduled task using Spring's ThreadPoolTaskScheduler.
 */
@Service
public class LeadOutreachTask {

    private final Logger logger = LoggerFactory.getLogger(LeadOutreachTask.class);

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

    /**
     * Initializes the scheduler on application startup.
     * Schedules the outreach task to run at a fixed rate based on polling interval.
     */
    @PostConstruct
    public void init() {
        scheduler.initialize();
        scheduler.scheduleAtFixedRate(this::runOutreachTask, Duration.ofMillis(taskConfig.getPollingIntervalMillis()));
    }

    /*
     * Executes the lead outreach task:
     * - Retrieves leads due for follow-up within the configured time window.
     * - Sends SMS follow-ups to each lead via the Dispatcher.
     * - Updates engagement metadata such as next follow-up time and stage.
     */
    private void runOutreachTask() {
        logger.info("Task Executed: {}", LocalDateTime.now());

        List<Lead> leadList = engagementManager.scanForFollowUpLeads(taskConfig.getFollowUpWindowMinutes());
        leadList.forEach(lead -> {
            dispatcher.executeSmsFollowUp(lead);
            engagementManager.handleFollowUpExecution(lead);
        });
    }

}
