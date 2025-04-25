package com.saleset.core.service.outreach;

import com.saleset.core.dao.AddressRepo;
import com.saleset.core.dao.MarketZipDataRepo;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.entities.MarketZipData;

import java.util.Optional;

public interface PhoneRoutingStrategy {

    String getLipsNumber();
    String getNypsNumber();
    MarketZipDataRepo getMarketZipDataRepo();
    AddressRepo getAddressRepo();

    default String determineFromNumber(Lead lead, Contact contact) {
        Optional<Address> optAddress = getAddressRepo().findAddressById(lead.getAddressId());
        if (optAddress.isEmpty() || optAddress.get().getZipCode() == null) {
            return fallbackByPhone(contact);
        }

        Optional<MarketZipData> optMzd = getMarketZipDataRepo().findByAddress(optAddress.get());
        if (optMzd.isEmpty()) {
            return fallbackByPhone(contact);
        }

        String county = optMzd.get().getCounty();
        return ("SUFFOLK".equals(county) || "NASSAU".equals(county)) ? getLipsNumber() : getNypsNumber();
    }

    private String fallbackByPhone(Contact contact) {
        String phone = contact.getPrimaryPhone();
        if (phone != null && (phone.startsWith("+1631") || phone.startsWith("+1516"))) {
            return getLipsNumber();
        }
        return getNypsNumber();
    }

}
