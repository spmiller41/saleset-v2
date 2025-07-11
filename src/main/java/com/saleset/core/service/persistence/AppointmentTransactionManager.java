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
     * Schedules an appointment for an existing lead using the booking UUID.
     * Internally calls upsertAppointment, which creates or updates the appointment
     * and marks the lead as CONVERTED.
     *
     * @param appointmentData details of the appointment, including the lead’s booking UUID
     * @return an Optional containing the AppointmentResult (lead + appointment) if the lead was found; otherwise Optional.empty()
     */
    @Transactional
    public Optional<AppointmentResult> scheduleAppointmentForExistingLead(AppointmentRequest appointmentData) {
        return leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID())
                .map(lead -> {
                    Appointment appointment = upsertAppointment(lead, appointmentData);
                    return new AppointmentResult(lead, appointment); // Lead already updated inside upsert
                });
    }

    /**
     * Creates or updates an appointment for the given lead. Always sets the lead stage to CONVERTED.
     * If an appointment already exists, updates its datetime; otherwise inserts a new one.
     *
     * @param lead the lead for whom the appointment is being created or updated
     * @param appointmentData the appointment details
     * @return the upserted Appointment
     */
    public Appointment upsertAppointment(Lead lead, AppointmentRequest appointmentData) {
        // Always set the lead to CONVERTED when booking an appointment
        lead.setCurrentStage(LeadStage.CONVERTED.toString());
        leadRepo.safeUpdate(lead);

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
