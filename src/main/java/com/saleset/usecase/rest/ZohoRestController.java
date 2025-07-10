package com.saleset.usecase.rest;

import com.saleset.usecase.dto.ZohoAppointmentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("v2/api")
public class ZohoRestController {

    @PostMapping("/create_appointment")
    public ResponseEntity<Map<String, String>> createAppointment(@RequestBody ZohoAppointmentRequest request) {



    }

}
