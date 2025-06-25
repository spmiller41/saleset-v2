package com.saleset.integration.zoho.util;

import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ZohoPayloadUtil {

    public static JSONObject buildAppointmentPayload(Appointment appointment, String zcrmSalesManagerId, ZohoModuleApiName zohoModuleApiName) {
        String appointmentField = zohoModuleApiName == ZohoModuleApiName.LEADS
                ? ZohoLeadFields.APPOINTMENT
                : ZohoLeadFields.MOST_RECENT_APPOINTMENT;

        JSONObject updatedFields = new JSONObject();
        updatedFields.put(appointmentField, ZohoUtils.formatDateTime(appointment.getStartDateTime()));
        updatedFields.put(ZohoLeadFields.OWNER, zcrmSalesManagerId);
        updatedFields.put(ZohoLeadFields.DESCRIPTION, ZohoUtils.buildDescription(appointment));

        JSONObject requestBody = new JSONObject();
        requestBody.put(ZohoLeadFields.DATA, new JSONArray().put(updatedFields));

        return requestBody;
    }

    public static JSONObject buildAppointmentPayload(Appointment appointment, Address address, String zcrmSalesManagerId, ZohoModuleApiName zohoModuleApiName) {
        String appointmentField = zohoModuleApiName == ZohoModuleApiName.LEADS
                ? ZohoLeadFields.APPOINTMENT
                : ZohoLeadFields.MOST_RECENT_APPOINTMENT;

        JSONObject updatedFields = new JSONObject();
        updatedFields.put(appointmentField, ZohoUtils.formatDateTime(appointment.getStartDateTime()));
        updatedFields.put(ZohoLeadFields.OWNER, zcrmSalesManagerId);
        updatedFields.put(ZohoLeadFields.DESCRIPTION, ZohoUtils.buildDescription(appointment));
        updatedFields.put(ZohoLeadFields.STREET, address.getStreet());
        updatedFields.put(ZohoLeadFields.CITY, address.getCity());
        updatedFields.put(ZohoLeadFields.STATE, address.getState());
        updatedFields.put(ZohoLeadFields.ZIP_CODE, address.getZipCode());

        JSONObject requestBody = new JSONObject();
        requestBody.put(ZohoLeadFields.DATA, new JSONArray().put(updatedFields));

        return requestBody;
    }

    public static JSONObject buildLeadCreatePayload(AppointmentRequest appointmentData,
                                                    String zcrmSalesManagerId, String ambassadorName) {
        JSONObject createFields = new JSONObject();
        createFields.put(ZohoLeadFields.OWNER, zcrmSalesManagerId);
        createFields.put(ZohoLeadFields.APPOINTMENT, ZohoUtils.formatDateTime(appointmentData.getStartDateTime()));
        createFields.put(ZohoLeadFields.PRODUCT1, new JSONArray().put(ZohoLeadFields.PRODUCT1_DEFAULT_VALUE));
        createFields.put(ZohoLeadFields.DESCRIPTION, ZohoUtils.buildDescription(appointmentData));
        createFields.put(ZohoLeadFields.LEAD_SOURCE, ZohoLeadFields.LEAD_SOURCE_DEFAULT_VALUE);
        createFields.put(ZohoLeadFields.SUB_SOURCE, ZohoLeadFields.SUB_SOURCE_DEFAULT_VALUE);
        createFields.put(ZohoLeadFields.AMBASSADOR_PROMOTER, ambassadorName);
        createFields.put(ZohoLeadFields.FIRST_NAME, appointmentData.getFirstName());
        createFields.put(ZohoLeadFields.LAST_NAME, appointmentData.getLastName());
        createFields.put(ZohoLeadFields.EMAIL, appointmentData.getEmail());
        createFields.put(ZohoLeadFields.PHONE, appointmentData.getPhone());
        createFields.put(ZohoLeadFields.MOBILE, appointmentData.getPhone());
        createFields.put(ZohoLeadFields.STREET, appointmentData.getStreet());
        createFields.put(ZohoLeadFields.CITY, appointmentData.getCity());
        createFields.put(ZohoLeadFields.STATE, appointmentData.getState());
        createFields.put(ZohoLeadFields.ZIP_CODE, appointmentData.getZip());

        JSONObject requestBody = new JSONObject();
        requestBody.put(ZohoLeadFields.DATA, new JSONArray().put(createFields));

        return requestBody;
    }

}
