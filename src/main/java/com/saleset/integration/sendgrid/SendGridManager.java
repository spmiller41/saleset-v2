package com.saleset.integration.sendgrid;

import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.util.DateTimeFormatting;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridManager {

    private static final Logger logger = LoggerFactory.getLogger(SendGridManager.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${ambassador.email}")
    private String ambassadorEmail;

    @Value("${booking.temp.host}")
    private String bookingTempHost;

    @Value("${booking.temp.customer}")
    private String bookingTempCustomer;

    @Value("${new.lead.notification}")
    private String newLeadNotification;

    @Value("${follow.up.notification}")
    private String followUpNotification;

    @Value("${dropbox.file.request.link}")
    private String dropboxLink;

    /*
     * Sends a booking confirmation email to the host when an appointment is booked via the CRM system by the booking manager.
     * This method populates an email template with dynamic content derived from the lead's information and the appointment timestamp.
     * The personalized content includes the customer's name, address, email, and CRM lead ID, along with the formatted date and time of the appointment.
     * The method also handles sending the email through SendGrid and checks for successful delivery.
     *
     * @param lead                 The lead entity containing the customer's details.
     * @param appointmentTimestamp The timestamp of the appointment, formatted as a string.
     * @param dynamicTemplateId    The ID of the SendGrid email template to use.
     * @param fromEmail            The email address from which the email is sent.
     * @param toEmail              The email address to which the email is sent.
     * @return                     The SendGrid Response object indicating the result of the email sending operation, or null in case of failure.
     */
    public Response sendBookingConfirmationHost(
            Lead lead, Contact contact, Address address, Appointment appointment) {

        Mail mail = new Mail();
        Personalization personalization = new Personalization();

        personalization.addDynamicTemplateData("customer_first_name", contact.getFirstName());
        personalization.addDynamicTemplateData("customer_last_name", contact.getLastName());
        personalization.addDynamicTemplateData("street", address.getStreet());
        personalization.addDynamicTemplateData("city", address.getCity());
        personalization.addDynamicTemplateData("state", address.getState());
        personalization.addDynamicTemplateData("zip", address.getZipCode());
        personalization.addDynamicTemplateData("customer_email", contact.getEmail());
        personalization.addDynamicTemplateData("customer_phone", contact.getPrimaryPhone());
        personalization.addDynamicTemplateData("crm_lead_id", lead.getZcrmExternalId());
        personalization.addDynamicTemplateData("date", DateTimeFormatting.toFormattedDate(appointment.getStartDateTime()));
        personalization.addDynamicTemplateData("time", DateTimeFormatting.toFormattedTime(appointment.getStartDateTime()));
        personalization.addDynamicTemplateData("appointment_type", appointment.getAppointmentType());


        // Set personalization
        mail.addPersonalization(personalization);

        // Replace with your dynamic template ID from SendGrid
        mail.setTemplateId(bookingTempHost);

        // Send Email
        try {
            Response response = sendDynamicEmail(personalization, mail, ambassadorEmail, ambassadorEmail);
            // Check if the response code indicates success (200-299)
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return response;
            } else {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }



    /*
     * Sends a booking confirmation email to the customer using a specified SendGrid template.
     * This method configures an email with personalized details such as the customer's name, appointment date,
     * time, and type. It also includes a Dropbox link for the customer to upload their energy bill.
     * The email is then sent using dynamic template data, and the sending status is logged.
     *
     * @param appointment The customer's appointment details, including name and appointment times.
     * @param dynamicTemplateId The ID for the SendGrid dynamic template used for the email.
     * @param dropboxLink A link for the customer to upload their energy bill.
     * @param fromEmail The email address from which the confirmation will be sent.
     * @param toEmail The customer's email address to which the confirmation will be sent.
     * @return A SendGrid Response object indicating the result of the email send attempt, or null if an error occurs.
     */
    public Response sendBookingConfirmationCustomer(Contact contact, Appointment appointment) {

        Mail mail = new Mail();
        Personalization personalization = new Personalization();

        personalization.addDynamicTemplateData("customer_first_name", contact.getFirstName());
        personalization.addDynamicTemplateData("date", DateTimeFormatting.toFormattedDate(appointment.getStartDateTime()));
        personalization.addDynamicTemplateData("time", DateTimeFormatting.toFormattedTime(appointment.getStartDateTime()));
        personalization.addDynamicTemplateData("appointment_type", appointment.getAppointmentType());
        personalization.addDynamicTemplateData("dropbox_link", dropboxLink);

        // Set personalization
        mail.addPersonalization(personalization);

        // Replace with your dynamic template ID from SendGrid
        mail.setTemplateId(bookingTempCustomer);

        // Send Email
        try {
            Response response = sendDynamicEmail(personalization, mail, contact.getEmail(), ambassadorEmail);
            // Check if the response code indicates success (200-299)
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return response;
            } else {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }




    /*
     * Sends a notification email to a specified recipient about a new lead using a SendGrid dynamic template.
     * @param lead The LeadEntity object containing the lead's information.
     * @param dynamicTemplateId The ID of the SendGrid dynamic template to be used for the email.
     * @param fromEmail The sender's email address.
     * @param toEmail The recipient's email address.
     * @return A SendGrid Response object indicating the result of the email send attempt.
     */
    public Response sendNewLeadNotification(Lead lead, Contact contact, Address address) {
        Mail mail = new Mail();
        Personalization personalization = new Personalization();

        String customerAddress = String.format("%s, %s, %s, %s",
                address.getStreet(), address.getCity(), address.getState(), address.getZipCode());

        String customerFullName = String.format("%s %s", contact.getFirstName(), contact.getLastName());

        personalization.addDynamicTemplateData("customer_address", customerAddress);
        personalization.addDynamicTemplateData("customer_name", customerFullName);
        personalization.addDynamicTemplateData("customer_phone", contact.getPrimaryPhone());
        personalization.addDynamicTemplateData("lead_source", lead.getLeadSource());
        personalization.addDynamicTemplateData("sub_source", lead.getSubSource());

        // Set personalization
        mail.addPersonalization(personalization);

        // Replace with your dynamic template ID from SendGrid
        mail.setTemplateId(newLeadNotification);

        // Send Email
        try {
            Response response = sendDynamicEmail(personalization, mail, ambassadorEmail, ambassadorEmail);
            // Check if the response code indicates success (200-299)
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return response;
            } else {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }




    public Response sendFollowUpCallAlert(Lead lead, Contact contact, Address address) {
        Mail mail = new Mail();
        Personalization personalization = new Personalization();

        String customerAddress = String.format("%s, %s, %s, %s",
                address.getStreet(), address.getCity(), address.getState(), address.getZipCode());

        String customerFullName = String.format("%s %s", contact.getFirstName(), contact.getLastName());

        personalization.addDynamicTemplateData("customer_address", customerAddress);
        personalization.addDynamicTemplateData("customer_name", customerFullName);
        personalization.addDynamicTemplateData("customer_phone", contact.getPrimaryPhone());
        personalization.addDynamicTemplateData("customer_email", contact.getEmail());
        personalization.addDynamicTemplateData("lead_source", lead.getLeadSource());
        personalization.addDynamicTemplateData("sub_source", lead.getSubSource());
        personalization.addDynamicTemplateData("crm_lead_id", lead.getZcrmExternalId());

        // Set personalization
        mail.addPersonalization(personalization);

        // Replace with your dynamic template ID from SendGrid
        mail.setTemplateId(followUpNotification);

        // Send Email
        try {
            Response response = sendDynamicEmail(personalization, mail, ambassadorEmail, ambassadorEmail);
            // Check if the response code indicates success (200-299)
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("Follow-up call email attempt. Response: {}", response.getBody());
                return response;
            } else {
                logger.error("Follow-up email failed. Response: {}", response.getBody());
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }




    /*
     * Prepares and sends an email with the specified personalization's and template.
     * The email is sent through the SendGrid API using the provided Mail object.
     *
     * @param personalization The personalization details to be included in the email.
     * @param mail The Mail object containing email configurations.
     * @param to The recipient's email address.
     * @return Response The response from the SendGrid API after sending the email.
     * @throws IOException If an error occurs during communication with the SendGrid API.
     */
    private Response sendDynamicEmail(Personalization personalization, Mail mail, String to, String from) throws IOException {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);
        mail.setFrom(fromEmail);
        personalization.addTo(toEmail);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        return sg.api(request);
    }



}
