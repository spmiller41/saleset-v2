package com.saleset;

import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
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
	private ContactRepo contactRepo;

	@Autowired
	private QueryUrlGenerator queryUrlGenerator;

	@Autowired
	private TwilioManager twilioManager;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			Optional<Lead> optLead = leadRepo.findLeadByUUID("1bb2b4b8-3294-4175-ad91-567240d449ff");
			optLead.ifPresent(lead -> {
				Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
				optContact.ifPresent(contact -> {
					// Lips Number: +15166892144
					Message message = twilioManager.sendSMS(
							"+15166892144",
							contact.getPrimaryPhone(),
							"Testing: " + lead.getTrackingWebhookUrl());
					System.out.println(message.getBody());
				});
			});
		};
	}
	*/

}