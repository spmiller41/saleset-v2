package com.saleset.core.rest;

import com.saleset.core.dto.AppointmentDataTransfer;
import com.saleset.core.service.transaction.AppointmentTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/api")
public class AppointmentRestController {

    @Autowired
    private AppointmentTransactionManager appointmentTransactionManager;

    @PostMapping("/create_appointment")
    public void createAppointment(@RequestBody AppointmentDataTransfer appointmentData) {
        appointmentTransactionManager.createAppointmentForLeadUuid(appointmentData);
    }

}
