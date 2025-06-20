package com.saleset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

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


