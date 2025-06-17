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
import com.saleset.integration.zoho.dto.response.ZohoLeadFetchResponse;
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
			testRequest.setStreet("633 Test St");
			testRequest.setCity("Fake Town");
			testRequest.setState("NY");
			testRequest.setZip("11980");
			testRequest.setBookingReference("bookingReference");
			testRequest.setBookingSource("Booking_Link");

			ZohoLeadCreateResponse response = zohoLeadService.createLead(testRequest);
			if (!response.getResponseCode().equals("DUPLICATE_DATA")) {
				zohoLeadService.fetchLead(response.getZohoLeadId()).ifPresent(fetchedZohoLead -> {
					LeadRequest leadData = new LeadRequest(
							testRequest,
							fetchedZohoLead.getId(),
							fetchedZohoLead.getAutoNumber(),
							ZohoLeadFields.LEAD_SOURCE_DEFAULT_VALUE,
							ZohoLeadFields.SUB_SOURCE_DEFAULT_VALUE,
							LeadStage.CONVERTED.toString());

					Optional<Lead> optLead = leadRepo.findLeadByExternalId(fetchedZohoLead.getId());

					if (optLead.isPresent()) {
						Lead lead = optLead.get();
						lead.setCurrentStage(LeadStage.CONVERTED.toString());
						leadRepo.safeUpdate(lead);

						Optional<Appointment> optAppointment = appointmentRepo.findAppointmentByLead(lead);
						if (optAppointment.isPresent()) {

						}

						Address address = new Address(testRequest);

						Appointment appointment = new Appointment(testRequest, lead);
						appointmentRepo.safeInsert(appointment);
					} else {

					}
				});
			}

			if (response.getResponseCode().equals("DUPLICATE_DATA")) {
				Optional<Lead> optLead = leadRepo.findLeadByExternalId(response.getZohoLeadId());

				if (optLead.isPresent()) {
					Lead lead = optLead.get();
					lead.setCurrentStage(LeadStage.CONVERTED.toString());
					leadRepo.safeUpdate(lead);

					Address address = new Address(testRequest);

					Appointment appointment = new Appointment(testRequest, lead);
					appointmentRepo.safeInsert(appointment);

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

							addressRepo.findAddressById(lead.getId()).ifPresent(address -> {
								zohoLeadService.updateLeadAppointment(appointment, address, leadData.getZcrmExternalId());
							});
						});
					});
				}
			}


		};
	}


}


