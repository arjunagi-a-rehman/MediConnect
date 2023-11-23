package com.Arjunagi.DoctorApp.controller;

import com.Arjunagi.DoctorApp.models.doctorRecord.MedicalSpecialization;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.models.dtos.DoctorDataOutPutDTO;
import com.Arjunagi.DoctorApp.models.dtos.VerificationDataDto;
import com.Arjunagi.DoctorApp.services.DoctorRecoredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.TreeMap;

@RestController
public class DoctorRecordController {
    @Autowired
    DoctorRecoredService doctorRecoredService;
    @PutMapping("user/doc/verification/data")
    public String addUpdateDocInfoToRecord(@RequestBody VerificationDataDto verificationDataDto){
        return doctorRecoredService.addDocInfoToRecordForVerification(verificationDataDto);
    }
    @GetMapping("doctors/{value}")
    public List<DoctorDataOutPutDTO> getAllVerifiedDoctorsData(@PathVariable String value){
        return doctorRecoredService.getAllVerifiedDoctorsData(value);
    }
    @GetMapping("doctors/specialization")
    public List<DoctorDataOutPutDTO> getAllBySpecialization(@PathVariable String value, @RequestParam MedicalSpecialization specialization){
        return doctorRecoredService.getAllBySpecialization(value,specialization);
    }
    @GetMapping("doctors/city/{value}")
    public List<DoctorDataOutPutDTO> getAllByCity(@PathVariable String value,@RequestParam Double latitude,@RequestParam Double longitude){
        return doctorRecoredService.getAllByCity(value,latitude,longitude);
    }
    @GetMapping("doctors/distant/{value}")
    public TreeMap<Double,DoctorDataOutPutDTO> getAllDocByDistance(@PathVariable String value,@RequestParam Double latitude,@RequestParam Double longitude){
        return doctorRecoredService.getAllDocByDistance(value,latitude,longitude);
    }

}
