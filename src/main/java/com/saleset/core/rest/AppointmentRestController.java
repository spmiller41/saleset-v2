package com.saleset.core.rest;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Contact;
import com.saleset.integration.sendgrid.SendGridManager;
import com.saleset.usecase.ExternalLeadAppointmentHandler;
import com.saleset.usecase.InternalLeadAppointmentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("v2/api")
public class AppointmentRestController {

    @Autowired
    private InternalLeadAppointmentHandler internalLeadAppointmentHandler;

    @Autowired
    private ExternalLeadAppointmentHandler externalLeadAppointmentHandler;

    @Autowired
    private LeadRepo leadRepo;

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private SendGridManager sendGridManager;

    @PostMapping("/create_appointment_internal")
    public void createAppointmentInternal(@RequestBody AppointmentRequest appointmentData) {
        internalLeadAppointmentHandler.syncLeadAppointment(appointmentData);

        leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID()).ifPresent(lead -> {

            Address address;
            Optional<Address> optAddress = addressRepo.findAddressByLead(lead);
            address = optAddress.orElse(new Address());

            Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
            if (optContact.isEmpty()) return;

            Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);
            if (optAppointment.isEmpty()) return;

            sendGridManager.sendBookingConfirmationHost(lead, optContact.get(), address, optAppointment.get());
            sendGridManager.sendBookingConfirmationCustomer(optContact.get(), optAppointment.get());

        });
    }

    @PostMapping("/create_appointment_external")
    public void createAppointmentExternal(@RequestBody AppointmentRequest appointmentData) {
        externalLeadAppointmentHandler.syncLeadAppointment(appointmentData);

        leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID()).ifPresent(lead -> {

            Address address;
            Optional<Address> optAddress = addressRepo.findAddressByLead(lead);
            address = optAddress.orElse(new Address());

            Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
            if (optContact.isEmpty()) return;

            Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);
            if (optAppointment.isEmpty()) return;

            sendGridManager.sendBookingConfirmationHost(lead, optContact.get(), address, optAppointment.get());
            sendGridManager.sendBookingConfirmationCustomer(optContact.get(), optAppointment.get());

        });

    }

}
