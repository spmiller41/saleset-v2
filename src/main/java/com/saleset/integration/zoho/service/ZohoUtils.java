package com.saleset.integration.zoho.service;

import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class ZohoUtils {

    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return dateTime.atZone(ZoneId.of("America/New_York")).format(formatter);
    }

    public static HttpHeaders buildHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return headers;
    }

    public static String buildEndpoint(String zcrmApiBaseUrl, String zcrmLeadId) {
        return String.format("%s%s/%s", zcrmApiBaseUrl, ZohoModuleApiName.LEADS, zcrmLeadId);
    }

    public static String buildDescription(AppointmentRequest appointmentData) {
        String formattedAppointment =
                appointmentData.getStartDateTime().format(DateTimeFormatter.ofPattern("EEEE, M/d/yy 'at' h:mma"));
        return String.format("%s for %s", appointmentData.getAppointmentType(), formattedAppointment);
    }

}
