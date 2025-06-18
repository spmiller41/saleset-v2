package com.saleset;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.AppointmentRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;
import com.saleset.core.service.persistence.leads.LeadEntryPipelineManager;
import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.integration.zoho.dto.response.ZohoLeadCreateResponse;
import com.saleset.integration.zoho.service.ZohoLeadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication
@EnableScheduling
public class SalesetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesetApplication.class, args);
	}

	@Autowired
	private ZohoLeadsService zohoLeadService;

	@Autowired
	private LeadRepo leadRepo;

	@Autowired
	private AppointmentRepo appointmentRepo;

	@Autowired
	private AddressRepo addressRepo;

	@Autowired
	private LeadEntryPipelineManager entryPipeline;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {

			AppointmentRequest testRequest = new AppointmentRequest();
			testRequest.setStartDateTime(LocalDateTime.now().plusDays(3));
			testRequest.setEndDateTime(LocalDateTime.now().plusDays(3).plusMinutes(60));
			testRequest.setAppointmentType("Virtual Meeting");
			testRequest.setFirstName("Sean");
			testRequest.setLastName("Te$t");
			testRequest.setEmail("sean.tester@example.com");
			testRequest.setPhone("+16318895508");
			testRequest.setStreet("144 Test St");
			testRequest.setCity("Test City");
			testRequest.setState("NY");
			testRequest.setZip("11980");
			testRequest.setBookingReference("bookingReference");
			testRequest.setBookingSource("Booking_Link");

			// Attempt to create lead in zcrm
			ZohoLeadCreateResponse response = zohoLeadService.createLead(testRequest);

			// Is this lead in already in zrm...
			if (!response.getResponseCode().equals("DUPLICATE_DATA")) {
				// fetch the lead from zcrm...
				zohoLeadService.fetchLead(response.getZohoLeadId()).ifPresent(fetchedZohoLead -> {
					// check if the lead already exists via the zcrm lead id...
					Optional<Lead> optLead = leadRepo.findLeadByExternalId(fetchedZohoLead.getId());

					// if the lead is present...
					if (optLead.isPresent()) {
						// grab the lead...
						Lead lead = optLead.get();
						// set the lead to converted...
						lead.setCurrentStage(LeadStage.CONVERTED.toString());
						// and update the lead...
						leadRepo.safeUpdate(lead);

						// now that the lead is updated to converted, check if there's an appointment for this lead...
						Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);

						// if there's an appointment present...
						if (optAppointment.isPresent()) {
							// grab the appointment...
							Appointment appointment = optAppointment.get();
							// update the appointment date/time...
							appointment.updateAppointmentDateTime(testRequest);
							appointmentRepo.safeUpdate(appointment);
						} else {
							// if no appointment is present, create the appointment for the lead...
							Appointment appointment = new Appointment(testRequest, lead);
							appointmentRepo.safeInsert(appointment);
						}

						// this lead could have utilized a different address, make sure zcrm know this...
						Address address = new Address(testRequest);
						// generate an appointment locally to feed to zcrm...
						Appointment appointment = new Appointment(testRequest, lead);
						// update zcrm lead with appointment...
						zohoLeadService.updateLeadAppointment(appointment, address, response.getZohoLeadId());
					} else {
						// ... this lead doesn't currently exist in our system so run it through the entry pipeline as CONVERTED
						LeadRequest leadData = new LeadRequest(
								testRequest,
								fetchedZohoLead.getId(),
								fetchedZohoLead.getAutoNumber(),
								ZohoLeadFields.LEAD_SOURCE_DEFAULT_VALUE,
								ZohoLeadFields.SUB_SOURCE_DEFAULT_VALUE,
								LeadStage.CONVERTED.toString());
						entryPipeline.manageLead(leadData);

						// ... grab the lead after it's handled by the entry pipeline...
						leadRepo.findLeadByExternalId(fetchedZohoLead.getId()).ifPresent(lead -> {
							// create the appointment for the CONVERTED lead
							Appointment appointment = new Appointment(testRequest, lead);
							appointmentRepo.safeInsert(appointment);

							// grab the address mapped to the lead and update the zcrm record
							addressRepo.findAddressById(lead.getId()).ifPresent(address ->
									zohoLeadService.updateLeadAppointment(appointment, address, leadData.getZcrmExternalId()));
						});
					}
				});
			}

			if (response.getResponseCode().equals("DUPLICATE_DATA")) {
				Optional<Lead> optLead = leadRepo.findLeadByExternalId(response.getZohoLeadId());

				if (optLead.isPresent()) {
					Lead lead = optLead.get();
					lead.setCurrentStage(LeadStage.CONVERTED.toString());
					leadRepo.safeUpdate(lead);

					// now that the lead is updated to converted, check if there's an appointment for this lead...
					Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);

					// if there's an appointment present...
					if (optAppointment.isPresent()) {
						// grab the appointment...
						Appointment appointment = optAppointment.get();
						// update the appointment date/time...
						appointment.updateAppointmentDateTime(testRequest);
						appointmentRepo.safeUpdate(appointment);
					} else {
						// if no appointment is present, create the appointment for the lead...
						Appointment appointment = new Appointment(testRequest, lead);
						appointmentRepo.safeInsert(appointment);
					}

					Address address = new Address(testRequest);
					Appointment appointment = new Appointment(testRequest, lead);
					zohoLeadService.updateLeadAppointment(appointment, address, lead.getZcrmExternalId());
				} else {
					zohoLeadService.fetchLead(response.getZohoLeadId()).ifPresent(fetchedZohoLead -> {
						LeadRequest leadData = new LeadRequest(
								testRequest,
								fetchedZohoLead.getId(),
								fetchedZohoLead.getAutoNumber(),
								ZohoLeadFields.LEAD_SOURCE_DEFAULT_VALUE,
								ZohoLeadFields.SUB_SOURCE_DEFAULT_VALUE,
								LeadStage.CONVERTED.toString());
						entryPipeline.manageLead(leadData);

						leadRepo.findLeadByExternalId(fetchedZohoLead.getId()).ifPresent(lead -> {
							Appointment appointment = new Appointment(testRequest, lead);
							appointmentRepo.safeInsert(appointment);

							addressRepo.findAddressById(lead.getId()).ifPresent(address ->
									zohoLeadService.updateLeadAppointment(appointment, address, leadData.getZcrmExternalId()));
						});
					});
				}
			}


		};
	}


}


