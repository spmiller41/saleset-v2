package com.saleset.core.rest;

import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.service.persistence.leads.LeadEntryPipelineManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/api")
public class LeadsRestController {

    private final LeadEntryPipelineManager transactionManager;

    @Autowired
    public LeadsRestController(LeadEntryPipelineManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @PostMapping("/create_lead")
    public void createLead(@RequestBody LeadRequest leadData) {
        System.out.println(leadData);
        transactionManager.manageLead(leadData);
    }

}
