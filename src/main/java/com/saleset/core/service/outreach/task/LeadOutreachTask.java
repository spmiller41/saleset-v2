package com.saleset.core.service.outreach.task;

import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.outreach.Dispatcher;
import com.saleset.core.service.persistence.leads.LeadEngagementManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * - Filters out any leads in excluded stages (e.g. DNC or Converted) as a safety check.
     * - Ensures each contact receives only one follow-up per run, even if tied to multiple leads.
     * - Sends SMS follow-ups and email alerts via the Dispatcher.
     * - Updates engagement metadata such as next-follow-up time, stage, and follow-up count.
     */
    private void runOutreachTask() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Task Executed: {}", now);

        // 1) Fetch and log the raw list
        List<Lead> leadList = engagementManager.scanForFollowUpLeads(taskConfig.getFollowUpWindowMinutes());
        logger.info("→ {} leads returned for follow-up window of {} minutes",
                leadList.size(), taskConfig.getFollowUpWindowMinutes());
        leadList.forEach(lead ->
                logger.debug("   • Lead[id={} contactId={} stage='{}' nextFollowUp={}]",
                        lead.getId(), lead.getContactId(), lead.getCurrentStage(), lead.getNextFollowUp())
        );

        Set<Integer> contactedContactIds = new HashSet<>();

        leadList.stream()
                // 2) Belt & suspenders: log each before checking exclusion
                .peek(lead -> logger.debug("Checking exclusion for Lead[id={} stage='{}']",
                        lead.getId(), lead.getCurrentStage()))
                .filter(lead -> {
                    String rawStage = lead.getCurrentStage();
                    String stage = (rawStage != null ? rawStage.trim() : "");
                    boolean isExcluded = EXCLUDED_STAGES.contains(stage);
                    if (isExcluded) {
                        logger.info("→ Skipping excluded-stage Lead[id={} stage='{}']", lead.getId(), stage);
                    }
                    return !isExcluded;
                })
                // 3) Log after filter how many remain
                .peek(lead -> logger.debug("Proceeding with Lead[id={} stage='{}']",
                        lead.getId(), lead.getCurrentStage()))
                .forEach(lead -> {
                    // 4) Only one follow-up per contact
                    if (contactedContactIds.add(lead.getContactId())) {
                        logger.info("Dispatching follow-up for Lead[id={} contactId={}]",
                                lead.getId(), lead.getContactId());
                        dispatcher.executeSmsFollowUp(lead);
                        dispatcher.executeFollowUpEmail(lead);
                    } else {
                        logger.debug("Already dispatched for contactId={} — skipping SMS & email for Lead[id={}]",
                                lead.getContactId(), lead.getId());
                    }

                    // 5) Always update the engagement metadata
                    logger.debug("Updating engagement metadata for Lead[id={}]", lead.getId());
                    engagementManager.handleFollowUpExecution(lead);
                });
    }

    private static final Set<String> EXCLUDED_STAGES = Set.of(
            LeadStage.DNC.toString(),
            LeadStage.CONVERTED.toString()
    );

}
