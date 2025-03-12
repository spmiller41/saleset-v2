package com.saleset;

import com.saleset.core.dao.ContactRepo;
import com.saleset.core.dao.LeadRepo;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.util.QueryUrlGenerator;
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

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			Optional<Lead> optLead = leadRepo.findLeadByUUID("43d01a1f-f5b1-47c1-8cc8-52eb75859610");
			optLead.ifPresent(lead -> {
				Optional<Contact> optContact = contactRepo.findContactById(lead.getContactId());
				optContact.ifPresent(contact -> {
					String url = queryUrlGenerator.buildTracking(lead, contact);
					System.out.println(url);
				});
			});
		};
	}
	*/


}