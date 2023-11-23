package com.Arjunagi.DoctorApp.models.appontment;

import com.Arjunagi.DoctorApp.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    private LocalDateTime creatingTimeStamp;
    private LocalDateTime scheduledTimeStamp;
    private LocalDateTime appointmentEntTime;

    private String symptoms;

    private String diagnostics;

    private String prescription;
    @ManyToOne
    @JoinColumn(name = "fkPatientId")
    private User patient;
    @ManyToOne
    @JoinColumn(name = "fkDocId")
    private User doctor;
    @Enumerated(value = EnumType.STRING)
    private AppointmentStatus status;

    private Double Bp;

    private Double sugar;
    public Appointment(LocalDateTime scheduledTimeStamp, LocalDateTime appointmentEntTime, String symptoms, User patient, User doctor) {
        this.scheduledTimeStamp = scheduledTimeStamp;
        this.appointmentEntTime = appointmentEntTime;
        this.symptoms = symptoms;
        this.patient = patient;
        this.doctor = doctor;
        creatingTimeStamp=LocalDateTime.now();
        status=AppointmentStatus.BOOKED;
    }
}
