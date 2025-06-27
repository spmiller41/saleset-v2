package com.saleset.core.service.persistence;

import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.AppointmentResult;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
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
     * Upserts an appointment for an existing lead (identified by its booking UUID):
     * if an appointment already exists, updates its date/time; otherwise inserts a new one.
     * Then marks the lead as CONVERTED and returns both the updated lead and the upserted appointment.
     *
     * @param appointmentData the appointment details, including the lead’s booking UUID
     * @return an Optional containing the AppointmentResult (lead + appointment) if the lead was found; otherwise Optional.empty()
     */
    @Transactional
    public Optional<AppointmentResult> scheduleAppointmentForExistingLead(AppointmentRequest appointmentData) {
        return leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID())
                .flatMap(lead -> {
                    Appointment appointment = upsertAppointment(lead, appointmentData);

                    lead.setCurrentStage(LeadStage.CONVERTED.toString());
                    return leadRepo.safeUpdate(lead)
                            .map(updatedLead -> {
                                logger.info("Lead stage updated to CONVERTED for Lead: {}", updatedLead.getId());
                                // 3) return both the updated lead and the appointment
                                return new AppointmentResult(updatedLead, appointment);
                            });
                });
    }

    public Appointment upsertAppointment(Lead lead, AppointmentRequest appointmentData) {
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

}
