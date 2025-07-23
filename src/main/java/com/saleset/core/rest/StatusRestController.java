package com.saleset.core.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/api")
public class StatusRestController {

    @GetMapping("/status")
    public String getStatus() { return "Saleset is up and is running."; }

}
