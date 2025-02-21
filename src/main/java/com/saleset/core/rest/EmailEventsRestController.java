package com.saleset.core.rest;

import com.saleset.core.dto.SGEventDataTransfer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v2/api/email_events")
public class EmailEventsRestController {

    @PostMapping
    public void emailEvent(@RequestBody List<SGEventDataTransfer> eventDataList) {
        eventDataList.forEach(System.out::println);
    }

}
