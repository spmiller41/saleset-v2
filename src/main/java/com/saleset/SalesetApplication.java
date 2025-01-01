package com.saleset;

import com.saleset.core.dao.AddressDao;
import com.saleset.core.dao.ContactDao;
import com.saleset.core.dao.EventDao;
import com.saleset.core.dao.LeadDao;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication
public class SalesetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesetApplication.class, args);
	}

	/*
	@Autowired
	private ContactDao contactDao;

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private LeadDao leadDao;

	@Autowired
	private EventDao eventDao;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {

			Contact contact = new Contact();
			contact.setFirstName("John");
			contact.setLastName("Doe");
			contact.setEmail("johnd123@testing.com");
			contact.setPhone1("+15162134401");
			Optional<Contact> optContact = contactDao.safeInsert(contact);
			optContact.ifPresent(newContact -> System.out.println("Contact Inserted: " + newContact));


			Address address = new Address();
			address.setStreet("144 Fake St");
			address.setCity("Test City");
			address.setState("New York");
			address.setZipCode("00551");
			Optional<Address> optAddress = addressDao.safeInsert(address);
			optAddress.ifPresent(newAddress -> System.out.println("Address Inserted: " + newAddress));


			Lead lead = new Lead();
			optContact.ifPresent(contactData -> lead.setContactId(contactData.getId()));
			optAddress.ifPresent(addressData -> lead.setAddressId(addressData.getId()));
			lead.setStage("New");
			lead.setCreatedAt(LocalDateTime.now());
			lead.setStageUpdatedAt(LocalDateTime.now());
			lead.setPreviousFollowUp(LocalDateTime.now().plusSeconds(13));
			lead.setNextFollowUp(LocalDateTime.now().plusDays(1).plusHours(5).plusMinutes(17));
			lead.setLeadSource("Internet");
			lead.setSubSource("Solar Insight");
			lead.setUuid("0011-1244-11111-23243");
			lead.setExternalId("zcrm_3880966000000087501");
			lead.setBookingPageUrl("https://lipower-youcanbook.me");
			Optional<Lead> optLead = leadDao.safeInsert(lead);
			optLead.ifPresent(newLead -> System.out.println("Lead Inserted: " + newLead));

			Event event = new Event();
			optLead.ifPresent(leadData -> event.setLeadId(leadData.getId()));
			event.setEventType("Click");
			event.setCreatedAt(LocalDateTime.now().plusMinutes(17));
			event.setDayOfWeek("Wednesday");
			event.setPeriodOfDay("Morning");
			Optional<Event> optEvent = eventDao.safeInsert(event);
			optEvent.ifPresent(newEvent -> System.out.println("Event Inserted: " + newEvent));

		};
	}
	*/

}
