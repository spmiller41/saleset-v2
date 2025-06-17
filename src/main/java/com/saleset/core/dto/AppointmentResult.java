package com.saleset.core.dto;

import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;

public record AppointmentResult(Lead lead, Appointment appointment) {}
