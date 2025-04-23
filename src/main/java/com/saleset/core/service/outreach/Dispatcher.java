package com.saleset.core.service.outreach;

import com.saleset.core.entities.Lead;
import com.saleset.core.service.sms.TwilioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Dispatcher {

    private final TwilioManager twilioManager;


    @Autowired
    public Dispatcher(TwilioManager twilioManager) {
        this.twilioManager = twilioManager;
    }

    public void executeFollowUp(Lead lead) {

    }

}
