package com.saleset;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Lead;
import com.saleset.usecase.InternalLeadAppointmentHandler;
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
	private LeadRepo leadRepo;

	@Autowired
	private InternalLeadAppointmentHandler internalLeadAppointmentHandler;

	@Bean
	public CommandLineRunner demo() {

		return (args) -> {
			leadRepo.findLeadById(1).ifPresent(lead -> {
				AppointmentRequest appointmentData = buildTestData(lead);
				internalLeadAppointmentHandler.handleInternalAppointment(appointmentData);
			});

		};

	}

	private AppointmentRequest buildTestData(Lead lead) {
		AppointmentRequest appointmentData = new AppointmentRequest();
		appointmentData.setLeadBookingUUID(lead.getUuid());
		appointmentData.setBookingReference("YCBM");
		appointmentData.setBookingReference("ycbm-test-01");
		appointmentData.setAppointmentType("Virtual Meeting");
		appointmentData.setEmail("seantesting@test.com");
		appointmentData.setPhone("+16318895508");
		appointmentData.setFirstName("Sean");
		appointmentData.setLastName("Te$t");
		appointmentData.setStreet("144 Test St");
		appointmentData.setCity("Test City");
		appointmentData.setState("NY");
		appointmentData.setZip("11980");
		appointmentData.setStartDateTime(LocalDateTime.now().plusDays(4));
		appointmentData.setEndDateTime(LocalDateTime.now().plusDays(4).plusHours(1));
		return appointmentData;
	}
	*/

}



