package com.Arjunagi.DoctorApp.models;

import com.Arjunagi.DoctorApp.models.appontment.Appointment;
import com.Arjunagi.DoctorApp.models.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private BloodGroup bloodGroup;
    private Double height;
    private Double weight;
    private String emergencyContact;
    private LocalDate dateOfBirth;
    @OneToOne
    @JoinColumn(name = "fkPatientId")
    private User patient;
    @OneToMany
    @JoinColumn(name = "fkPatientRecordId")
    private List<Appointment> appointments;
    @OneToMany
    @JoinColumn(name = "fkPatientRecordId")
    private List<Documents> patientDocuments;
    public PatientRecord(User patient) {
        this.patient = patient;
    }
}
