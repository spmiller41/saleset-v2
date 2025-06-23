package com.saleset.core.rest;

import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.usecase.InternalLeadAppointmentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/api")
public class AppointmentRestController {

    @Autowired
    private InternalLeadAppointmentHandler internalLeadAppointmentHandler;

    @PostMapping("/create_appointment")
    public void createAppointment(@RequestBody AppointmentRequest appointmentData) {
        internalLeadAppointmentHandler.handleInternalAppointment(appointmentData);
    }

}
