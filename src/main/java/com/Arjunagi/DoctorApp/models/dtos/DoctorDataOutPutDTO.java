package com.Arjunagi.DoctorApp.models.dtos;

import com.Arjunagi.DoctorApp.models.Address;
import com.Arjunagi.DoctorApp.models.doctorRecord.DoctorQualification;
import com.Arjunagi.DoctorApp.models.doctorRecord.MedicalSpecialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDataOutPutDTO {
    private Integer docId;
    private String name;
    private DoctorQualification qualification;
    private MedicalSpecialization specialization;
    private Double consultingFee;
    private Integer experience;
    private Address address;
}
