package com.saleset;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.dto.request.LeadRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.service.persistence.leads.LeadEntryPipelineManager;
import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.integration.zoho.dto.response.ZohoLeadUpsertResponse;
import com.saleset.integration.zoho.service.ZohoDealsService;
import com.saleset.integration.zoho.service.ZohoLeadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class SalesetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesetApplication.class, args);
	}

	/*
	@Autowired
	private LeadEntryPipelineManager leadEntryPipelineManager;

	@Autowired
	private ZohoLeadsService zohoLeadsService;

	@Autowired
	private ZohoDealsService zohoDealsService;

	@Autowired
	private LeadRepo leadRepo;

	@Bean
	public CommandLineRunner demo() {

		return (args) -> {


			LeadRequest leadRequest = new LeadRequest();
			leadRequest.setFirstName("Sean");
			leadRequest.setLastName("Te$t");
			leadRequest.setStreet("254 Dogwood Road West");
			leadRequest.setCity("Mastic Beach");
			leadRequest.setState("NY");
			leadRequest.setZipCode("11951");
			leadRequest.setLeadSource("Internet PPC");
			leadRequest.setSubSource("Google");
			leadRequest.setPrimaryPhone("6318895508");
			leadRequest.setEmail("seantesting@test.com");
			leadRequest.setStage("New");
			leadRequest.setZcrmExternalId("3880966000334300145");
			leadRequest.setZcrmAutoNumber("90569");

			// leadEntryPipelineManager.manageLead(leadRequest);

			Appointment appointment = new Appointment();
			appointment.setAppointmentType("Virtual Meeting");
			appointment.setBookingSource("YCBM");
			appointment.setBookingReference("ycbm-test-01");
			appointment.setStartDateTime(LocalDateTime.now().plusDays(5));

			leadRepo.findLeadById(1).ifPresent(lead -> {
				ZohoLeadUpsertResponse response = zohoLeadsService.updateLeadAppointment(appointment, lead);
				System.out.println(response);

				if (response.isInvalidData()) {
					zohoDealsService.fetchDeal(ZohoLeadFields.AUTO_NUMBER, "90569").ifPresent(fetchResponse -> {
						zohoDealsService.updateDealAppointment(appointment, new Address(leadRequest), fetchResponse);
					});
				}
			});

		};

	}
	 */



}


