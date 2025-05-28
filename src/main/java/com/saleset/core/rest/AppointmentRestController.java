package com.saleset.core.rest;

import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.AppointmentDataTransfer;
import com.saleset.core.entities.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/api")
public class AppointmentRestController {

    @Autowired
    private LeadRepo leadRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @PostMapping("/create_appointment")
    public void createAppointment(@RequestBody AppointmentDataTransfer appointmentData) {
        System.out.println("Appointment Set. Data: " + appointmentData);

        leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID()).ifPresent(lead -> {
            Appointment appointment = new Appointment(appointmentData, lead);
            appointmentRepo.safeInsert(appointment).ifPresent(newAppointment -> {
                System.out.println("Appointment Inserted Successfully: " + newAppointment);
            });
        });
    }

}
