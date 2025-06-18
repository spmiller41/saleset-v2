package com.saleset.usecase;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.service.persistence.leads.LeadEntryPipelineManager;
import com.saleset.integration.zoho.service.ZohoLeadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalLeadAppointmentHandler {

    private final ZohoLeadsService zohoLeadService;
    private final LeadRepo leadRepo;
    private final AppointmentRepo appointmentRepo;
    private final AddressRepo addressRepo;
    private final LeadEntryPipelineManager entryPipeline;

    @Autowired
    public ExternalLeadAppointmentHandler(ZohoLeadsService zohoLeadService,
                                          LeadRepo leadRepo,
                                          AppointmentRepo appointmentRepo,
                                          AddressRepo addressRepo,
                                          LeadEntryPipelineManager entryPipeline) {
        this.zohoLeadService = zohoLeadService;
        this.leadRepo = leadRepo;
        this.appointmentRepo = appointmentRepo;
        this.addressRepo = addressRepo;
        this.entryPipeline = entryPipeline;
    }
}
