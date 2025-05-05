package com.saleset.core.service.transaction.leads;

import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.engine.EngagementEngineImpl;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Handles engagement-related operations for leads, including resumption logic,
 * follow-up scheduling, and determining optimal contact times using the Engagement Engine.
 * <p>
 * This service is responsible for:
 * <ul>
 *   <li>Identifying leads ready for follow-up within a given time window</li>
 *   <li>Resuming engagement for re-entering leads based on matching address or context</li>
 *   <li>Updating lead follow-up schedules and stage prioritization</li>
 * </ul>
 *
 * Typically called by lead intake pipelines or schedulers managing follow-up workflows.
 */

@Service
public class LeadEngagementManager implements LeadWorkflowManager {

    private final Logger logger = LoggerFactory.getLogger(LeadEngagementManager.class);

    private final LeadRepo leadRepo;
    private final EngagementEngineImpl engagementEngine;
    private final EventRepo eventRepo;

    @Autowired
    public LeadEngagementManager(LeadRepo leadRepo, EngagementEngineImpl engagementEngine, EventRepo eventRepo) {
        this.leadRepo = leadRepo;
        this.engagementEngine = engagementEngine;
        this.eventRepo = eventRepo;
    }


    /**
     * Scans for leads that are due for follow-up within the given time window.
     * Excludes leads in terminal stages like DNC and CONVERTED.
     *
     * @param timeWindowMinutes The number of minutes into the future to look for eligible leads.
     * @return List of leads ready for follow-up within the time window.
     */
    @Transactional
    public List<Lead> scanForFollowUpLeads(int timeWindowMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(timeWindowMinutes);

        List<String> excludedStages = List.of(LeadStage.DNC.toString(), LeadStage.CONVERTED.toString());

        return leadRepo.findLeadsReadyForFollowUp(now, endTime, excludedStages);
    }


    /**
     * Retrieves the booking page URL associated with a lead by its UUID.
     *
     * @param leadUUID The unique identifier of the lead.
     * @return The booking page URL, or null if the lead is not found.
     */
    @Transactional
    public String getBookingPageUrl(String leadUUID) {
        Optional<Lead> optLead = leadRepo.findLeadByUUID(leadUUID);
        return optLead.map(Lead::getBookingPageUrl).orElse(null);
    }


    /**
     * Attempts to update the follow-up process for a lead when the incoming address matches
     * the lead’s existing address and the lead is eligible for engagement.
     *
     * @param leadData The new lead submission data.
     * @param address The matched address from the new submission.
     * @param lead The existing lead to resume.
     */
    @Transactional
    public void updateLeadEngagementProcess(LeadDataTransfer leadData, Address address, Lead lead) {
        if (isExistingAddress(address, leadData) && isValidForUpdate(lead)) {
            List<Event> eventList = eventRepo.findByLead(lead);
            updateLeadEngagement(lead, eventList, "Lead reentry - Contains Address. Update successful");
        }
    }


    /**
     * Attempts to update the follow-up process for a lead that has no associated address validation
     * but is otherwise eligible for continued engagement.
     *
     * @param lead The existing lead to resume follow-up for.
     */
    @Transactional
    public void updateLeadEngagementProcess(Lead lead) {
        if (isValidForUpdate(lead)) {
            List<Event> eventList = eventRepo.findByLead(lead);
            updateLeadEngagement(lead, eventList, "Lead reentry - No Address Update successful");
        }
    }


    /*
     * Updates a lead's next follow-up date and stage based on engagement history and
     * optimal time calculation. Also persists and logs the update.
     *
     * @param lead The lead to update.
     * @param eventList List of engagement events associated with the lead.
     * @param logMessage Custom log message to indicate the context of the update.
     */
    private void updateLeadEngagement(Lead lead, List<Event> eventList, String logMessage) {
        LocalDate targetDate = LocalDate.now().plusDays(1);
        LocalTime targetTime = engagementEngine.determineFollowUpTime(
                lead.getPreviousFollowUp(), targetDate, eventList);
        LocalDateTime nextFollowUp = LocalDateTime.of(targetDate, targetTime);

        lead.setNextFollowUp(nextFollowUp);
        lead.setCurrentStage(LeadStage.AGED_HIGH_PRIORITY.toString());

        Optional<Lead> optUpdatedLead = leadRepo.safeUpdate(lead);
        optUpdatedLead.ifPresent(updatedLead -> logger.info("{}: {}", logMessage, updatedLead));
    }

}
