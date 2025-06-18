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
import com.saleset.usecase.ExternalLeadAppointmentHandler;
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

	/*

	@Autowired
	private ExternalLeadAppointmentHandler externalLeadAppointmentHandler;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {

			AppointmentRequest testRequest = new AppointmentRequest();
			testRequest.setStartDateTime(LocalDateTime.now().plusDays(10));
			testRequest.setEndDateTime(LocalDateTime.now().plusDays(10).plusMinutes(60));
			testRequest.setAppointmentType("Virtual Meeting");
			testRequest.setFirstName("Sean");
			testRequest.setLastName("Te$t");
			testRequest.setEmail("sean.tester@example.com");
			testRequest.setPhone("+16318895508");
			testRequest.setStreet("333 Test St");
			testRequest.setCity("Test City");
			testRequest.setState("NY");
			testRequest.setZip("11980");
			testRequest.setBookingReference("bookingReference_1");
			testRequest.setBookingSource("Booking_Link");

			externalLeadAppointmentHandler.handleExternalAppointment(testRequest);

		};
	}

	*/

}


