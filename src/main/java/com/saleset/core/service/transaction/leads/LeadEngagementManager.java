package com.saleset.core.service.transaction.leads;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LeadEngagementManager {

    private final LeadRepo leadRepo;

    @Autowired
    public LeadEngagementManager(LeadRepo leadRepo) {
        this.leadRepo = leadRepo;
    }

    @Transactional
    public List<Lead> scanForFollowUpLeads(int timeWindowMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(timeWindowMinutes);

        List<String> excludedStages = List.of(LeadStage.DNC.toString(), LeadStage.CONVERTED.toString());

        return leadRepo.findLeadsReadyForFollowUp(now, endTime, excludedStages);
    }

    @Transactional
    public String getBookingPageUrl(String leadUUID) {
        Optional<Lead> optLead = leadRepo.findLeadByUUID(leadUUID);
        return optLead.map(Lead::getBookingPageUrl).orElse(null);
    }

}
