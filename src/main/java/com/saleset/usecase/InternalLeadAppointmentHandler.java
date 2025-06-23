package com.saleset.usecase;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Appointment;
import com.saleset.core.service.persistence.AppointmentTransactionManager;
import com.saleset.integration.zoho.service.ZohoLeadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalLeadAppointmentHandler {

    private final AppointmentTransactionManager appointmentTransactionManager;
    private final ZohoLeadsService zohoLeadsService;
    private final LeadRepo leadRepo;

    @Autowired
    public InternalLeadAppointmentHandler(AppointmentTransactionManager appointmentTransactionManager,
                                          ZohoLeadsService zohoLeadsService, LeadRepo leadRepo) {
        this.appointmentTransactionManager = appointmentTransactionManager;
        this.zohoLeadsService = zohoLeadsService;
        this.leadRepo = leadRepo;
    }

    public void handleInternalAppointment(AppointmentRequest appointmentData) {
        appointmentTransactionManager.scheduleAppointmentForExistingLead(appointmentData)
                .ifPresent(appointmentResult -> {
                    zohoLeadsService.updateLeadAppointment(appointmentResult.appointment(), appointmentResult.lead());
                });
    }

}
