package com.saleset.core.service.transaction;

import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.AppointmentDataTransfer;
import com.saleset.core.entities.Appointment;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Transactional
    public void createAppointmentForLeadUuid(AppointmentDataTransfer appointmentData) {
        leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID()).ifPresent(lead -> {
            Appointment appointment = new Appointment(appointmentData, lead);
            appointmentRepo.safeInsert(appointment).ifPresent(newAppointment -> {
                logger.info("Appointment Inserted Successfully: {}" , newAppointment);
            });
        });
    }

}
