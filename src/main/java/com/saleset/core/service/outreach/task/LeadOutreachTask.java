package com.saleset.core.service.outreach.task;

import com.saleset.core.dao.LeadRepo;
import com.saleset.core.service.outreach.Dispatcher;
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
