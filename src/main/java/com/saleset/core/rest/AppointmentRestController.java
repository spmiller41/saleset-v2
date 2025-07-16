package com.saleset.core.rest;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Contact;
import com.saleset.core.service.persistence.leads.LeadEngagementManager;
import com.saleset.integration.sendgrid.SendGridManager;
import com.saleset.usecase.ExternalLeadAppointmentHandler;
import com.saleset.usecase.InternalLeadAppointmentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("v2/api")
public class AppointmentRestController {

    private final Logger logger = LoggerFactory.getLogger(AppointmentRestController.class);

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
    public ResponseEntity<String> createAppointmentInternal(@RequestBody AppointmentRequest appointmentData) {
        logger.info("Internal createAppointment called with payload: {}", appointmentData);

        try {
            internalLeadAppointmentHandler.syncLeadAppointment(appointmentData);
        } catch (Exception e) {
            logger.error("Failed to sync internal lead appointment for UUID {}",
                    appointmentData.getLeadBookingUUID(), e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Failed to sync appointment");
        }

        return leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID())
                .map(lead -> {
                    logger.info("Found lead {} — sending notifications", lead.getId());

                    Address address = addressRepo.findAddressByLead(lead).orElse(new Address());
                    Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
                    Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);

                    if (optContact.isEmpty()) {
                        logger.warn("Contact not found for lead {}", lead.getId());
                        return ResponseEntity.unprocessableEntity()
                                .body("Contact not found");
                    }
                    if (optAppointment.isEmpty()) {
                        logger.warn("Appointment record not found for lead {}", lead.getId());
                        return ResponseEntity.unprocessableEntity()
                                .body("Appointment not found");
                    }

                    sendGridManager.sendBookingConfirmationHost(lead, optContact.get(), address, optAppointment.get());
                    sendGridManager.sendBookingConfirmationCustomer(optContact.get(), optAppointment.get());

                    logger.info("Internal appointment workflow completed for lead {}", lead.getId());
                    return ResponseEntity.ok("Internal appointment created");
                })
                .orElseGet(() -> {
                    logger.warn("Lead not found for UUID {}", appointmentData.getLeadBookingUUID());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Lead not found");
                });
    }

    @PostMapping("/create_appointment_external")
    public ResponseEntity<String> createAppointmentExternal(@RequestBody AppointmentRequest appointmentData) {
        logger.info("External createAppointment called with payload: {}", appointmentData);

        try {
            externalLeadAppointmentHandler.syncLeadAppointment(appointmentData);
        } catch (Exception e) {
            logger.error("Failed to sync external lead appointment for UUID {}",
                    appointmentData.getLeadBookingUUID(), e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Failed to sync appointment");
        }

        return leadRepo.findLeadByUUID(appointmentData.getLeadBookingUUID())
                .map(lead -> {
                    logger.info("Found lead {} — sending notifications", lead.getId());

                    Address address = addressRepo.findAddressByLead(lead).orElse(new Address());
                    Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
                    Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);

                    if (optContact.isEmpty()) {
                        logger.warn("Contact not found for lead {}", lead.getId());
                        return ResponseEntity.unprocessableEntity()
                                .body("Contact not found");
                    }
                    if (optAppointment.isEmpty()) {
                        logger.warn("Appointment record not found for lead {}", lead.getId());
                        return ResponseEntity.unprocessableEntity()
                                .body("Appointment not found");
                    }

                    sendGridManager.sendBookingConfirmationHost(lead, optContact.get(), address, optAppointment.get());
                    sendGridManager.sendBookingConfirmationCustomer(optContact.get(), optAppointment.get());

                    logger.info("External appointment workflow completed for lead {}", lead.getId());
                    return ResponseEntity.ok("External appointment created");
                })
                .orElseGet(() -> {
                    logger.warn("Lead not found for UUID {}", appointmentData.getLeadBookingUUID());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Lead not found");
                });
    }

}
