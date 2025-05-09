package com.saleset.core.service.outreach;

import com.saleset.core.dao.LeadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeadOutreachTask {

    private final Dispatcher dispatcher;
    private final LeadRepo leadRepo;

    @Autowired
    public LeadOutreachTask(Dispatcher dispatcher, LeadRepo leadRepo) {
        this.dispatcher = dispatcher;
        this.leadRepo = leadRepo;
    }


}
