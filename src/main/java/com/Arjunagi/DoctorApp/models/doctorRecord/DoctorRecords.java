package com.Arjunagi.DoctorApp.models.doctorRecord;

import com.Arjunagi.DoctorApp.models.Address;
import com.Arjunagi.DoctorApp.models.Documents;
import com.Arjunagi.DoctorApp.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "fkDoctorId")
    @JsonIgnore
    private User doctor;
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
    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;
    private Boolean OnHoliday;
    @OneToMany
    @JoinColumn(name = "fkDoctorRecordId")
    @JsonIgnore
    private List<Documents> documentsList;
    public DoctorRecords(User doctor){
        this.doctor=doctor;
    }

}
