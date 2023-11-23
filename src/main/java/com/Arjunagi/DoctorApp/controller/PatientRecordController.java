package com.Arjunagi.DoctorApp.controller;

import com.Arjunagi.DoctorApp.models.BloodGroup;
import com.Arjunagi.DoctorApp.models.PatientRecord;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.services.PatientRecordServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class PatientRecordController {
    @Autowired
    PatientRecordServices patientRecordServices;

    @GetMapping("record/{value}")
    public PatientRecord getRecord(@PathVariable String value, @RequestParam(required = false) Integer patientId){
        return patientRecordServices.getRecord(value,patientId);
    }
    @PutMapping("record")
    public String addUpdatePatientDetails(@RequestBody AuthInpDto inpDto, @RequestParam(required = false) Double height, @RequestParam(required = false) Double weight, @RequestParam(required = false) BloodGroup bloodGroup, @RequestParam(required = false) LocalDate dob,@RequestParam(required = false) String emergencyContact){
        return patientRecordServices.addUpdatePatientDetails(inpDto,height,weight,bloodGroup,emergencyContact);
    }
}
