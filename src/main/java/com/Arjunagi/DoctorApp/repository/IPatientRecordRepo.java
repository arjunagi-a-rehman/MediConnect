package com.Arjunagi.DoctorApp.repository;

import com.Arjunagi.DoctorApp.models.PatientRecord;
import com.Arjunagi.DoctorApp.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPatientRecordRepo extends JpaRepository<PatientRecord,Integer> {
    PatientRecord findByPatient(User user);
}
