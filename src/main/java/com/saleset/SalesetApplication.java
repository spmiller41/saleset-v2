package com.saleset;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.EventRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.dto.LeadDataTransfer;
import com.saleset.core.service.engine.EngagementEngineImpl;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
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
	@Bean
	public CommandLineRunner demo() {
		return (args) -> {

		};
	}
	*/

}



/*

Contact contact = new Contact();
contact.setFirstName("John");
contact.setLastName("Doe");
contact.setEmail("johnd123@testing.com");
contact.setPrimaryPhone("+15162134401");
Optional<Contact> optContact = contactRepo.safeInsert(contact);
optContact.ifPresent(newContact -> System.out.println("Contact Inserted: " + newContact));

Address address = new Address();
address.setStreet("144 Fake St");
address.setCity("Test City");
address.setState("New York");
address.setZipCode("00551");
Optional<Address> optAddress = addressRepo.safeInsert(address);
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

*/