package com.Arjunagi.DoctorApp.models.dtos;

import com.Arjunagi.DoctorApp.models.Address;
import com.Arjunagi.DoctorApp.models.doctorRecord.DoctorQualification;
import com.Arjunagi.DoctorApp.models.doctorRecord.MedicalSpecialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationDataDto {
    private AuthInpDto inpDto;
    private DoctorQualification qualification;
    private MedicalSpecialization specialization;
    private String licenseNumber;
    private Integer experience;
    private LocalTime startingHours;
    private LocalTime endingHours;
    private LocalTime breakStartingTime;
    private LocalTime breakEndingTime;
    private Integer timeRequiredPerPatientInMin;
    private Integer consultationFee;
    private Address hospitalAddress;
}
