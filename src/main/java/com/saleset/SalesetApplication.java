package com.saleset;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.integration.zoho.dto.response.ZohoLeadCreateUpdateResponse;
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

	/*
	@Bean
	public CommandLineRunner demo() {
		return (args) -> {};
	}
	*/

}

