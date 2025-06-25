package com.saleset.usecase;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.core.service.persistence.AppointmentTransactionManager;
import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.integration.zoho.dto.response.ZohoLeadUpsertResponse;
import com.saleset.integration.zoho.service.ZohoDealsService;
import com.saleset.integration.zoho.service.ZohoLeadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InternalLeadAppointmentHandler {

    private final AppointmentTransactionManager appointmentTransactionManager;
    private final ZohoLeadsService zohoLeadsService;
    private final ZohoDealsService zohoDealsService;

    @Autowired
    public InternalLeadAppointmentHandler(AppointmentTransactionManager appointmentTransactionManager,
                                          ZohoLeadsService zohoLeadsService,
                                          ZohoDealsService zohoDealsService) {
        this.appointmentTransactionManager = appointmentTransactionManager;
        this.zohoLeadsService = zohoLeadsService;
        this.zohoDealsService = zohoDealsService;
    }


    public void handleInternalAppointment(AppointmentRequest appointmentData) {
        appointmentTransactionManager.scheduleAppointmentForExistingLead(appointmentData)
                .ifPresent(appointmentResult -> {

                    ZohoLeadUpsertResponse zohoLeadUpdateResponse =
                            zohoLeadsService.updateLeadAppointment(
                                    appointmentResult.appointment(), appointmentResult.lead());

                    if (zohoLeadUpdateResponse.isInvalidData()) {
                        String zcrmAutoNumber = appointmentResult.lead().getZcrmAutoNumber();
                        zohoDealsService.fetchDeal(ZohoLeadFields.AUTO_NUMBER, zcrmAutoNumber)
                                .ifPresent(deal -> {
                                    Appointment appointment = appointmentResult.appointment();
                                    Address address = new Address(appointmentData);
                                    zohoDealsService.updateDealAppointment(appointment, address, deal);
                                });
                    }

                });
    }


}
