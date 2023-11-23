package com.Arjunagi.DoctorApp.controller;

import com.Arjunagi.DoctorApp.models.appontment.Appointment;
import com.Arjunagi.DoctorApp.models.appontment.AppointmentStatus;
import com.Arjunagi.DoctorApp.models.dtos.AppointmentDto;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("appointment")
    public String bookAppointment(@RequestBody AppointmentDto appointmentDto){
        return appointmentService.bookAppointment(appointmentDto);
    }
    @GetMapping("appointments")
    public List<Appointment> getAllForPatient(@RequestBody AuthInpDto inpDto){
        return appointmentService.getAllForUser(inpDto);
    }
    @PutMapping("appointments/{appointmentId}/cancel")
    public String cancelAppointment(@RequestBody AuthInpDto inpDto,@PathVariable Integer appointmentId){
        return appointmentService.cancelAppointment(inpDto,appointmentId);
    }
    @PutMapping("appointment/{appointmentId}/doctor")
    public String addDiagnosticAndPrescription(@RequestBody AuthInpDto inpDto,@PathVariable Integer appointmentId,@RequestParam String diagnostic,@RequestParam String prescription,@RequestParam Double bp,@RequestParam Double sugar){
        return appointmentService.addDiagnosticAndPrescription(inpDto,appointmentId,diagnostic,prescription,bp,sugar);
    }
    @GetMapping("booked/appointments")
    public List<Appointment> getAllBookedAppointments(@RequestBody AuthInpDto authInpDto){
        return appointmentService.getAllBookedAppointments(authInpDto, AppointmentStatus.BOOKED);
    }

}
