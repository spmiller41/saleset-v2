package com.saleset;

import com.saleset.core.dao.AddressDao;
import com.saleset.core.dao.ContactDao;
import com.saleset.core.dao.EventDao;
import com.saleset.core.dao.LeadDao;
import com.saleset.core.engine.EngagementEngineImpl;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Event;
import com.saleset.core.entities.Lead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class SalesetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesetApplication.class, args);
	}


	@Autowired
	private ContactDao contactDao;

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private LeadDao leadDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private EngagementEngineImpl engagementEngine;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {

			LocalDateTime createdAt = LocalDateTime.of(
					LocalDate.of(2025, 1, 3),
					LocalTime.of(13, 47));

			LocalDateTime previousFollowUp = LocalDateTime.of(
					LocalDate.of(2025, 1, 1),
					LocalTime.of(10, 22));
			LocalDate targetDate = engagementEngine.determineFollowUpDate(createdAt, 4.0);
			System.out.println("Follow-up Date: " + targetDate);
			List<Event> events = new ArrayList<>();


			Event event_1 = new Event();
			event_1.setEventType("Click");
			event_1.setCreatedAt(LocalDateTime.of(
					LocalDate.of(2024, 12, 29),
					LocalTime.of(11, 27)));
			event_1.setDayOfWeek("Sunday");
			event_1.setPeriodOfDay("Morning");
			events.add(event_1);

			/*
			Event event_2 = new Event();
			event_2.setEventType("Open");
			event_2.setCreatedAt(LocalDateTime.of(
					LocalDate.of(2024, 12, 30),
					LocalTime.of(8, 9)));
			event_2.setDayOfWeek("Monday");
			event_2.setPeriodOfDay("Morning");
			events.add(event_2);

			Event event_3 = new Event();
			event_3.setEventType("Click");
			event_3.setCreatedAt(LocalDateTime.of(
					LocalDate.of(2024, 12, 30),
					LocalTime.of(14, 27)));
			event_3.setDayOfWeek("Monday");
			event_3.setPeriodOfDay("Afternoon");
			events.add(event_3);

			Event event_4 = new Event();
			event_4.setEventType("Open");
			event_4.setCreatedAt(LocalDateTime.of(
					LocalDate.of(2024, 12, 28),
					LocalTime.of(20, 41)));
			event_4.setDayOfWeek("Saturday");
			event_4.setPeriodOfDay("Evening");
			events.add(event_4);

			Event event_5 = new Event();
			event_5.setEventType("Open");
			event_5.setCreatedAt(LocalDateTime.of(
					LocalDate.of(2024, 12, 27),
					LocalTime.of(11, 36)));
			event_5.setDayOfWeek("Friday");
			event_5.setPeriodOfDay("Morning");
			events.add(event_5);
			*/


			LocalTime determinedTime = engagementEngine.determineFollowUpTime(previousFollowUp, targetDate, events);
			System.out.println("NEXT FOLLOW-UP " + LocalDateTime.of(targetDate, determinedTime));

		};
	}


}



/*
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
*/