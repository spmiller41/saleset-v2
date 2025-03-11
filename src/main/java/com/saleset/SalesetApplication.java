package com.saleset;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.enums.PhoneLineType;
import com.saleset.core.service.engine.EngagementEngineImpl;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.service.sms.PhoneNumberDetails;
import com.saleset.core.service.sms.TwilioManager;
import com.saleset.core.util.BookingUrlGenerator;
import com.saleset.core.util.PhoneNumberNormalizer;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
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
	private BookingUrlGenerator bookingUrlGenerator;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			Optional<Lead> optLead = leadRepo.findLeadByUUID("43d01a1f-f5b1-47c1-8cc8-52eb75859610");
			optLead.ifPresent(lead -> {
				Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
				optContact.ifPresent(contact -> {
					String url = bookingUrlGenerator.build(lead, contact);
					System.out.println(url);
				});
			});
		};
	}
	*/

}