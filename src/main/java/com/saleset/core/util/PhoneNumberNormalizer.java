package com.saleset.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PhoneNumberNormalizer {

    private static final Logger logger = LoggerFactory.getLogger(PhoneNumberNormalizer.class);

    public static Optional<String> normalizeToE164(String phoneNumber) {
        if (phoneNumber == null) return Optional.empty();

        String digitsOnly = phoneNumber.replaceAll("\\D", "");

        if (digitsOnly.length() == 11 && digitsOnly.startsWith("1")) {
            // Already has a country code (e.g., 1 for the US/Canada)
            return Optional.of("+" + digitsOnly);
        } else if (digitsOnly.length() == 10) {
            // Add the default country code (+1 for US/Canada)
            return Optional.of("+1" + digitsOnly);
        } else {
            logger.error("Invalid phone number format: {}", phoneNumber);
            return Optional.empty();
        }
    }

}