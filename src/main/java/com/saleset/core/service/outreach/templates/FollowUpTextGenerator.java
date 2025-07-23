package com.saleset.core.service.outreach.templates;

import com.saleset.core.entities.Contact;
import com.saleset.core.entities.Lead;
import com.saleset.core.enums.LeadStage;

public class FollowUpTextGenerator {

    private final String AMBASSADOR_NAME = "Stacy";
    private final String SOLAR_INFO_LINK =
            "https://www.energy.gov/eere/solar/homeowners-guide-federal-tax-credit-solar-photovoltaics";

    private final String leadFirstName;
    private final String bookingLink;
    private final String periodOfDay;
    private final String dayOfWeek;
    private final int followUpCount;

    public FollowUpTextGenerator(Lead lead, Contact contact) {
        leadFirstName = contact.getFirstName();
        bookingLink = lead.getTrackingWebhookUrl();
        periodOfDay = TemplateUtil.getTimePeriodOfDay();
        dayOfWeek = TemplateUtil.getDayOfWeek();
        followUpCount = lead.getFollowUpCount();
    }

    public String build(Lead lead) {
        if (lead.getCurrentStage().equalsIgnoreCase(LeadStage.NEW.toString())) {
            return generateMessageNewLead(followUpCount);
        } else {
            return generateMessageRehashedLead(followUpCount);
        }
    }

    private String generateMessageNewLead(int contactCount) {
        return switch (contactCount) {
            case 0 -> String.format("""
                    Good %s %s, this is %s with Power Solutions. It's great to see your interest in solar energy, and I'd be happy to discuss the incentives and savings we offer.
                    
                    We provide a simple booking page if you would like to schedule an in-home or virtual assessment using the link below:
                    
                    %s
                    
                    If you have any questions, feel free to call or text back. I'd be happy to help. Thanks for your time, and I hope you enjoy the rest of your %s!""",
                    periodOfDay, leadFirstName, AMBASSADOR_NAME, bookingLink, dayOfWeek);

            case 1 -> String.format("""
                    Hello %s, hope you're having a good %s. This is %s from Power Solutions again. I'd love to discuss your interest in solar as numerous homeowners in your region are now scheduling their solar assessments with us.
                    
                    If you prefer, you may arrange your own virtual or in-home solar assessment, simply follow the link below to our booking page. Select a convenient date and time, and your details will be populated automatically. Just specify your preferred type of assessment.
                    
                    %s
                    
                    Enjoy the rest of your %s!""", leadFirstName, dayOfWeek, AMBASSADOR_NAME, bookingLink, periodOfDay);

            case 2 -> String.format("""
                    Hello %s, this is %s from Power Solutions, reaching out to wish you a pleasant %s. Upon reviewing our latest inquiries, it appears we have yet to schedule your solar assessment.
                    
                    We wanted to gently remind you that by clicking on the link below, you can easily book a time for either an in-home or virtual assessment, whichever suits your preference. And should you decide to opt-out of further communication, simply respond with 'STOP', and we will honor your request promptly.
                    
                    %s
                    
                    Looking forward to your response and hoping to assist you soon with your solar energy needs!""", leadFirstName, AMBASSADOR_NAME, periodOfDay, bookingLink);

            case 3 -> String.format("""
                    Greetings %s, %s here from Power Solutions, hoping your %s is going well. I noticed we still haven't had the chance to schedule your solar assessment yet.
                    
                    We're here to guide you through every step of the way, ensuring you have all the information you need about our solar solutions. To set up a time for your in-home or virtual assessment, just click the link below. And if you prefer not to receive further messages from us, a simple 'STOP' reply will do.
                    
                    %s
                    
                    Your journey towards sustainable energy is important to us, and we're eager to support you in making informed decisions. Looking forward to connecting soon!""", leadFirstName, AMBASSADOR_NAME, periodOfDay, bookingLink);

            case 4 -> String.format("""
                    Good %s, %s! %s from Power Solutions here. We haven't had the chance to connect about your potential solar project yet.
                    
                    Given the growing number of your neighbors making the switch, we thought it might be the perfect time to explore your options. You can click the link below to easily schedule an in-home or virtual assessment at your convenience.
                    
                    Booking Link: %s
                    
                    We're here to support your transition to solar energy and look forward to potentially adding your home to the list of those harnessing the power of the sun in your area.""", periodOfDay, leadFirstName, AMBASSADOR_NAME, bookingLink);

            case 5 -> String.format("""
                    Hey %s, it’s %s from Power Solutions. I’ve been going through my recent inquiries and would love to chat if you’re still considering it!
                    
                    Just in case you’re ready to dive deeper, our booking link is below. A quick conversation could light up new possibilities. Not on your mind anymore? A simple 'STOP' will let us know.
                    
                    %s
                    
                    Have a great %s!""", leadFirstName, AMBASSADOR_NAME, bookingLink, periodOfDay);

            case 6 -> String.format("""
                    Hi %s, it's %s here once more from Power Solutions. I was reviewing our recent inquiries and noticed yours. I'd genuinely love to chat about how solar can benefit you. If you're still interested, feel free to use the link below for direct access to my booking page, where you can schedule either an in-home or virtual assessment at your convenience.
                    
                    Booking Link Here -> %s
                    
                    Enjoy the rest of your %s!""", leadFirstName, AMBASSADOR_NAME, bookingLink, dayOfWeek);

            case 7 -> String.format("""
                    Good %s %s, it's %s from Power Solutions. Our appointment slots are quickly filling up. Here's a quick guide on going solar, including the 30 percent Federal Tax incentive: %s
                    
                    To access our booking page to choose an in-home or virtual assessment, you may use the link below:
                    %s
                    
                    Happy %s!""", periodOfDay, leadFirstName, AMBASSADOR_NAME, SOLAR_INFO_LINK, bookingLink, dayOfWeek);
            case 8 -> String.format("""
                    Hello %s, this is %s from Power Solutions again. I'd love to connect with you to discuss the benefits of solar.
                    
                    As you may know, the 30 percent federal tax incentive is ending at the end of this year. To reserve a timeslot, you may call or text back, or use the booking link below:
                    
                    %s
                    """, leadFirstName, AMBASSADOR_NAME, bookingLink);
            case 9 -> String.format("""
                    Hi %s, it's %s from Power Solutions again. I hope your %s is going well. I'm going through our recent inquiries and noticed that you're still in our system.
                    
                    Just wanted to let you know that you may reserve a timeslot to discuss solar by calling or texting back, or using the booking link below:
                    
                    %s
                    
                    """, leadFirstName, AMBASSADOR_NAME, periodOfDay, bookingLink);
            case 10 -> String.format("""
                    Good %s %s, %s from Power Solutions here. I'm going over our more recent solar inquiries and would love to connect to discuss the possibility of going solar.
                    
                    You may call or text back to reserve an appointment with us. If you prefer to set a date/time yourself, you may use the following booking link:
                    
                    %s
                    
                    """, periodOfDay, leadFirstName, AMBASSADOR_NAME, bookingLink);
            case 11 -> String.format("""
                    Hi %s, I hope your %s is going well. I wanted to forward some information regarding solar. You may check it out using this link here: %s
                    
                    To reserve a timeslot to discuss solar with us, you may use this booking link:
                    
                    %s
                    
                    Or feel free to call or text back at your convenience.
                    """, leadFirstName, dayOfWeek, SOLAR_INFO_LINK, bookingLink);
            case 12 -> String.format("""
                    Hello %s! %s from Power Solutions again. I just wanted to remind you that you may use the link below to reserve a timeslot with us. We would love to discuss the possibilities of going solar with you.
                    
                    %s
                    
                    If you prefer, you may call or text back and I'd be happy to help.
                    """, leadFirstName, AMBASSADOR_NAME, bookingLink);
            default -> String.format("""
                    Greetings, %s! It’s %s from Power Solutions. We’re still here to explore solar options with you. If you're up for a chat, our booking link below offers both in-home and virtual assessments at your convenience.
                    
                    %s
                    
                    Hope to connect soon!""", leadFirstName, AMBASSADOR_NAME, bookingLink);
        };
    }



    private String generateMessageRehashedLead(int contactCount) {
        return switch (contactCount) {
            case 0 -> String.format("""
                    Hi %s, this is %s from Power Solutions. I see that you inquired for solar a while back. The 30 percent solar tax credit may drop this year, so I was wondering if you were interested in qualifying - before it goes.
                    
                    We have generated a few new programs also. If you would like, you can book an assessment with the following link.
                    
                    Booking Link: %s
                    
                    Feel free to text back or call, enjoy the rest of your %s!""", leadFirstName, AMBASSADOR_NAME, bookingLink, dayOfWeek);

            case 1 -> String.format("""
                    Good %s, %s. It's %s from Power Solutions again. I appreciate your time. I recently sent you a text and wanted to include the link below that goes over the 30 percent tax credit in case you're interested.
                    
                    Solar Information: %s
                    
                    If you would like, you can schedule an in-home or virtual assessment, or phone consultation via the booking link below.
                    
                    Booking Link: %s
                    
                    If you need any assistance with scheduling an assessment or have any questions, you may call or text me back through this number.""", periodOfDay, leadFirstName, AMBASSADOR_NAME, SOLAR_INFO_LINK, bookingLink);


            case 2 -> String.format("""
                    Hello %s, just reaching out from Power Solutions again to see if you wanted to connect to discuss the possibilities of going solar.
                    
                    To schedule an assessment, you may use this link: %s
                    
                    I would love to answer any questions you may have.
                    
                    Best regards, %s.""", leadFirstName, bookingLink, AMBASSADOR_NAME);

            case 3 -> String.format("""
                    Hey %s, it's %s from Power Solutions. If you have any interest in getting this tex credit before it goes, feel free to text, call, or use the link below.
                    
                    Schedule here: %s""", leadFirstName, AMBASSADOR_NAME, bookingLink);

            case 4 -> String.format("""
                    Hi %s, %s here. I haven't heard back from you, so I just wanted to touch base and see if you were interested in discussing our solar programs.
                    
                    You may use this link to book an assessment (we offer in-home, virtual, telephone): %s
                    
                    If you would like to opt-out from further communication, just reply 'STOP', or let me know otherwise.""", leadFirstName, AMBASSADOR_NAME, bookingLink);
            case 5 -> String.format("""
                    Good %s %s, %s with Power Solutions again. With the 30 percent federal tax incentive leaving at the end of the year, I'd like to remind you that you may use the link below to reserve an appointment with us:
                    
                    %s
                    
                    Or you may call or text back at your convenience.
                    """, periodOfDay, leadFirstName, AMBASSADOR_NAME, bookingLink);
            case 6 -> String.format("""
                    Hi %s, I hope your %s is going well. I wanted to forward some information regarding solar. You may check it out using this link here: %s
                    
                    To reserve a timeslot to discuss solar with us, you may use this booking link:
                    
                    %s
                    
                    Or feel free to call or text back at your convenience.
                    """, leadFirstName, dayOfWeek, SOLAR_INFO_LINK, bookingLink);
            case 7 -> String.format("""
                    Good %s %s, it's %s from Power Solutions. Our appointment slots are quickly filling up. Here's a quick guide on going solar, including the 30 percent Federal Tax incentive: %s
                    
                    To access our booking page to choose an in-home or virtual assessment, you may use the link below:
                    %s
                    
                    Happy %s!""", periodOfDay, leadFirstName, AMBASSADOR_NAME, SOLAR_INFO_LINK, bookingLink, dayOfWeek);
            case 8 -> String.format("""
                    Hello %s, %s with Power Solutions here. I've been going over our older inquiries again and noticed you're still in our system. To discuss the possibilities of going solar, you may reserve a timeslot with the booking link below:
                    
                    %s
                    
                    You may also call or text back at your convenience and I'd be happy to help!""", leadFirstName, AMBASSADOR_NAME, bookingLink);
            default -> String.format("""
                    Greetings, %s! It’s %s from Power Solutions. We’re still here to explore solar options with you in your area. If you're up for a chat, our booking link below offers both in-home and virtual assessments at your convenience.
                    
                    %s
                    
                    Hope to connect soon!""", leadFirstName, AMBASSADOR_NAME, bookingLink);
        };
    }

}
