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
	@Bean
	public CommandLineRunner demo() {
		return (args) -> {

		};
	}
	*/

}












/*

	@Autowired
	private ContactRepo contactRepo;

	@Autowired
	private AddressRepo addressRepo;

	@Autowired
	private LeadRepo leadRepo;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			Optional<Lead> optLead = leadRepo.findLeadById(1);
			optLead.ifPresent(lead -> {
				Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
				Optional<Address> optAddress = addressRepo.findAddressById(lead.getAddressId());

				Address address = null;
				Contact contact = null;

				if (optContact.isPresent()) contact = optContact.get();
				if (optAddress.isPresent()) address = optAddress.get();

				if (address != null && contact != null) {
					Response response = sendGeneratedTemplate(contact, address, lead, "stacy@powersolutionsalerts.com");
					System.out.println(response.getHeaders());
				}

			});
		};
	}


	public Response sendGeneratedTemplate(Contact contact, Address address, Lead lead, String fromEmail) {
		Mail mail = new Mail();
		Personalization personalization = new Personalization();
		String toEmail = contact.getEmail();

		String dayOfWeek = LocalDateTime.now().getDayOfWeek().toString();
		String formattedDayOfWeek =
				dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();

		personalization.addDynamicTemplateData("to_first_name", contact.getFirstName());
		personalization.addDynamicTemplateData("from_first_name", "Stacy");
		personalization.addDynamicTemplateData("from_last_name", "Madison");
		personalization.addDynamicTemplateData("personal_booking_link", lead.getBookingPageUrl());
		personalization.addDynamicTemplateData("day_of_week", formattedDayOfWeek);
		personalization.addDynamicTemplateData("street", address.getStreet());
		personalization.addDynamicTemplateData("state", address.getState());
		personalization.addDynamicTemplateData("city", address.getCity());
		personalization.addDynamicTemplateData("ambassador_number", "(555) 555-5555");

		// Set personalization
		mail.addPersonalization(personalization);

		// Replace with your dynamic template ID from SendGrid
		mail.setTemplateId("d-ba68d13ddbce492eb668d1fed73e084a");

		// Send Email
		try {
			Response response = sendDynamicEmail(lead, personalization, mail, toEmail, fromEmail);
			// Check if the response code indicates success (200-299)
			if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
				return response;
			} else {
				System.out.println(response.getStatusCode());
				return null;
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}


	private Response sendDynamicEmail(Lead lead, Personalization personalization, Mail mail, String to, String from) throws IOException {
		Email fromEmail = new Email(from);
		Email toEmail = new Email(to);
		mail.setFrom(fromEmail);
		personalization.addTo(toEmail);

		// Attach the UUID to the email event tracking
		personalization.addCustomArg("lead_uuid", lead.getUuid());

		SendGrid sg = new SendGrid("");
		Request request = new Request();
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		request.setBody(mail.build());
		return sg.api(request);
	}

*/

