package com.saleset.core.service.persistence;

import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.AppointmentResult;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Appointment;
import com.saleset.core.enums.LeadStage;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppointmentTransactionManager {

    private final Logger logger = LoggerFactory.getLogger(AppointmentTransactionManager.class);

    private final AppointmentRepo appointmentRepo;
    private final LeadRepo leadRepo;

    @Autowired
    public AppointmentTransactionManager(AppointmentRepo appointmentRepo, LeadRepo leadRepo) {
        this.appointmentRepo = appointmentRepo;
        this.leadRepo = leadRepo;
    }

    /**
     * Schedules a new appointment for an existing lead (identified by its booking UUID),
     * marks the lead as CONVERTED, and returns both the updated lead and the new appointment.
     *
     * @param appointmentData the appointment details and lead booking UUID
     * @return an Optional containing the AppointmentResult (lead + appointment) if the lead was found and the appointment saved; otherwise Optional.empty()
     */
    @Transactional
    public Optional<AppointmentResult> scheduleAppointmentForExistingLead(AppointmentRequest appointmentData) {
        return leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID())
                .flatMap(lead -> {
                    // 1) create appointment
                    Appointment appointment = new Appointment(appointmentData, lead);
                    return appointmentRepo.safeInsert(appointment)
                            .flatMap(inserted -> {
                                logger.info("Appointment Inserted Successfully: {}", inserted);
                                // 2) update lead stage
                                lead.setCurrentStage(LeadStage.CONVERTED.toString());
                                return leadRepo.safeUpdate(lead)
                                        .map(updatedLead -> {
                                            logger.info("Lead stage updated to CONVERTED for Lead: {}", updatedLead.getId());
                                            // 3) return both
                                            return new AppointmentResult(updatedLead, inserted);
                                        });
                            });
                });
    }

}
