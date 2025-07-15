package com.saleset.core.rest;

import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.persistence.leads.LeadEntryPipelineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("v2/api")
public class LeadsRestController {

    private static final Logger logger = LoggerFactory.getLogger(LeadsRestController.class);

    private final LeadEntryPipelineManager transactionManager;
    private final LeadRepo leadRepo;
    private final ContactRepo contactRepo;

    @Autowired
    public LeadsRestController(LeadEntryPipelineManager transactionManager, LeadRepo leadRepo, ContactRepo contactRepo) {
        this.transactionManager = transactionManager;
        this.leadRepo = leadRepo;
        this.contactRepo = contactRepo;
    }

    @PostMapping("/create_lead")
    public void createLead(@RequestBody LeadRequest leadData) {
        transactionManager.manageLead(leadData);
    }

    @PostMapping("/lead_opt_out")
    public ResponseEntity<Map<String, String>> optOutLead(@RequestBody LeadRequest leadData) {
        Map<String, String> response = new HashMap<>();

        try {
            Optional<Lead> optLead = leadRepo.findLeadByExternalId(leadData.getZcrmExternalId());
            if (optLead.isEmpty()) {
                optLead = leadRepo.findLeadByAutoNumber(leadData.getZcrmAutoNumber());
                if (optLead.isEmpty()) {
                    response.put("status", "error");
                    response.put("message", "Lead not found");
                    return ResponseEntity.status(404).body(response);
                }
            }

            Optional<Contact> optContact = contactRepo.findContactById(optLead.get().getContactId());
            if (optContact.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Contact not found");
                return ResponseEntity.status(404).body(response);
            }

            List<Lead> associatedLeads = leadRepo.findLeadByContact(optContact.get());
            associatedLeads.forEach(lead -> {
                lead.setCurrentStage(LeadStage.DNC.toString());
                leadRepo.safeUpdate(lead);
            });

            response.put("status", "success");
            response.put("message", "Lead(s) successfully marked as DNC");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Unexpected error during processing the lead DNC", ex);
            response.put("status", "error");
            response.put("message", "Internal server error occurred.");
            return ResponseEntity.status(500).body(response);
        }
    }


}
