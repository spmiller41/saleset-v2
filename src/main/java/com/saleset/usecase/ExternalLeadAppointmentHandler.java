package com.saleset.usecase;

import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.persistence.leads.LeadEntryPipelineManager;
import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.integration.zoho.dto.response.ZohoFetchResponse;
import com.saleset.integration.zoho.dto.response.ZohoLeadCreateResponse;
import com.saleset.integration.zoho.service.ZohoLeadsService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the flow for incoming “call-in” appointments:
 * creates or de-dupes the Lead in Zoho, ensures a CONVERTED Lead locally,
 * upserts the Appointment, and on duplicates pushes the appointment & address back to Zoho.
 */
@Service
public class ExternalLeadAppointmentHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExternalLeadAppointmentHandler.class);

    private final ZohoLeadsService zohoLeadService;
    private final LeadRepo leadRepo;
    private final AppointmentRepo appointmentRepo;
    private final LeadEntryPipelineManager entryPipeline;

    @Autowired
    public ExternalLeadAppointmentHandler(ZohoLeadsService zohoLeadService,
                                          LeadRepo leadRepo,
                                          AppointmentRepo appointmentRepo,
                                          LeadEntryPipelineManager entryPipeline) {
        this.zohoLeadService = zohoLeadService;
        this.leadRepo = leadRepo;
        this.appointmentRepo = appointmentRepo;
        this.entryPipeline = entryPipeline;
    }

    /**
     * Handles an external appointment request end-to-end:
     * <p>
     * - Create or detect duplicate Lead in Zoho
     * <p>
     * - Fetch full Zoho Lead data
     * <p>
     * - Ensure a CONVERTED Lead exists in our DB
     * <p>
     * - Upsert the Appointment locally
     * <p>
     * - If duplicate, update the Zoho record with appointment & address
     *
     * @param appointmentData the incoming appointment details
     */
    @Transactional
    public void handleExternalAppointment(AppointmentRequest appointmentData) {
        ZohoLeadCreateResponse createLeadResponse = zohoLeadService.createLead(appointmentData);
        ZohoFetchResponse fetched = zohoLeadService.fetchLead(createLeadResponse.getZohoLeadId())
                .orElseThrow(() -> new IllegalStateException("Zoho lead not found"));

        Lead lead = ensureConvertedLead(appointmentData, fetched);
        Appointment appointment = upsertAppointment(lead, appointmentData);

        if (createLeadResponse.isDuplicate()) {
            zohoLeadService.updateLeadAppointment(appointment, new Address(appointmentData), fetched.getId());
        }
    }


    // Builds a new CONVERTED Lead locally using the entry pipeline and returns it
    private Lead buildNewConvertedLead(AppointmentRequest appointmentData, ZohoFetchResponse fetchedZohoLead) {
        LeadRequest leadData = new LeadRequest(
                appointmentData,
                fetchedZohoLead.getId(),
                fetchedZohoLead.getAutoNumber(),
                ZohoLeadFields.LEAD_SOURCE_DEFAULT_VALUE,
                ZohoLeadFields.SUB_SOURCE_DEFAULT_VALUE,
                LeadStage.CONVERTED.toString());
        entryPipeline.manageLead(leadData);

        return leadRepo.findLeadByExternalId(fetchedZohoLead.getId())
                .orElseThrow(() -> new IllegalStateException("Pipeline failed to create lead"));
    }


    // Inserts or updates the Appointment for the given Lead based on the request
    private Appointment upsertAppointment(Lead lead, AppointmentRequest appointmentData) {
        return appointmentRepo.findAppointmentByLead(lead)
                .map(existingAppointment -> {
                    existingAppointment.updateAppointmentDateTime(appointmentData);
                    appointmentRepo.safeUpdate(existingAppointment);
                    logger.info("Updated Appointment for Lead Id: {}", lead.getId());
                    return existingAppointment;
                })
                .orElseGet(() -> {
                    Appointment appointment = new Appointment(appointmentData, lead);
                    appointmentRepo.safeInsert(appointment);
                    logger.info("Inserted Appointment for Lead Id {}", lead.getId());
                    return appointment;
                });
    }


    // Finds or creates a CONVERTED Lead locally based on the fetched Zoho data
    private Lead ensureConvertedLead(AppointmentRequest appointmentData, ZohoFetchResponse fetched) {
        return leadRepo.findLeadByExternalId(fetched.getId())
                .map(this::markConverted)
                .orElseGet(() -> buildNewConvertedLead(appointmentData, fetched));
    }


    // Marks an existing local Lead as CONVERTED and persists the change
    private Lead markConverted(Lead lead) {
        lead.setCurrentStage(LeadStage.CONVERTED.toString());
        leadRepo.safeUpdate(lead);
        logger.info("Marked existing lead {} as CONVERTED", lead.getId());
        return lead;
    }

}
