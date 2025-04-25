package com.saleset;

import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dao.MarketZipDataRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.entities.MarketZipData;
import com.saleset.core.service.outreach.Dispatcher;
import com.saleset.core.service.sms.TwilioManager;
import com.saleset.core.util.QueryUrlGenerator;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class SalesetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesetApplication.class, args);
	}

	/*
	@Autowired
	private LeadRepo leadRepo;

	@Autowired
	private Dispatcher dispatcher;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			Optional<Lead> optLead = leadRepo.findLeadById(1);
			optLead.ifPresent(lead -> dispatcher.executeFollowUp(lead));
		};
	}
	*/

}
