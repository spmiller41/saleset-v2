package com.saleset.usecase.rest;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.persistence.AppointmentTransactionManager;
import com.saleset.core.service.persistence.leads.LeadEntryPipelineManager;
import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.usecase.dto.ZohoAppointmentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("v2/api")
public class ZohoRestController {

    private static final Logger logger = LoggerFactory.getLogger(ZohoRestController.class);

    private final LeadRepo leadRepo;
    private final LeadEntryPipelineManager leadEntryManager;
    private final AppointmentTransactionManager appointmentManager;

    @Autowired
    public ZohoRestController(LeadRepo leadRepo,
                              LeadEntryPipelineManager leadEntryManager,
                              AppointmentTransactionManager appointmentManager) {

        this.leadRepo = leadRepo;
        this.leadEntryManager = leadEntryManager;
        this.appointmentManager = appointmentManager;
    }




    @PostMapping("/create_appointment")
    public void createAppointment(@RequestBody ZohoAppointmentRequest request) {
        // Try lookup by external ID, then by auto-number
        Optional<Lead> optLead = leadRepo.findLeadByExternalId(request.getId())
                .or(() -> leadRepo.findLeadByAutoNumber(request.getAutoNumber()));

        Lead lead = optLead.orElseGet(() -> {
            // if neither lookup found anything, create the lead
            leadEntryManager.manageLead(buildInternalLeadRequest(request));
            return leadRepo.findLeadByExternalId(request.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Lead was just created but still not found: " + request.getId()
                    ));
        });

        // Upsert the appointment for whatever lead we ended up with
        Appointment appointment =
                appointmentManager.upsertAppointment(lead, buildInternalAppointmentRequest(request));

        logger.info("{} appointment for lead {} (externalId={}, autoNumber={})",
                optLead.isPresent() ? "Updated" : "Created and upserted",
                lead.getId(), request.getId(), request.getAutoNumber()
        );
    }




    public LeadRequest buildInternalLeadRequest(ZohoAppointmentRequest request) {
        LeadRequest internalLeadRequest = new LeadRequest();

        internalLeadRequest.setFirstName(request.getFirstName());
        internalLeadRequest.setLastName(request.getLastName());
        internalLeadRequest.setPrimaryPhone(request.getPhone());
        internalLeadRequest.setEmail(request.getEmail());
        internalLeadRequest.setStreet(request.getStreet());
        internalLeadRequest.setCity(request.getCity());
        internalLeadRequest.setState(request.getState());
        internalLeadRequest.setZipCode(request.getZipCode());
        internalLeadRequest.setLeadSource(ZohoLeadFields.LEAD_SOURCE_DEFAULT_VALUE);
        internalLeadRequest.setSubSource(ZohoLeadFields.SUB_SOURCE_DEFAULT_VALUE);
        internalLeadRequest.setZcrmExternalId(request.getId());
        internalLeadRequest.setZcrmAutoNumber(request.getAutoNumber());
        internalLeadRequest.setStage(LeadStage.CONVERTED.toString());

        return internalLeadRequest;
    }




    public AppointmentRequest buildInternalAppointmentRequest(ZohoAppointmentRequest request) {
        AppointmentRequest internalAppointmentRequest = new AppointmentRequest();

        internalAppointmentRequest.setStartDateTime(request.getAppointmentDateTime());
        internalAppointmentRequest.setEndDateTime(request.getAppointmentDateTime().plusHours(1));
        internalAppointmentRequest.setBookingSource("Ambassador");
        internalAppointmentRequest.setAppointmentType("N/A");

        return internalAppointmentRequest;
    }

}
