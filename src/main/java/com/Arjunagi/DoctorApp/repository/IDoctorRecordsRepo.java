package com.Arjunagi.DoctorApp.repository;

import com.Arjunagi.DoctorApp.models.doctorRecord.DoctorRecords;
import com.Arjunagi.DoctorApp.models.doctorRecord.MedicalSpecialization;
import com.Arjunagi.DoctorApp.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IDoctorRecordsRepo extends JpaRepository<DoctorRecords,Integer> {
    DoctorRecords findByDoctor(User doctor);
    List<DoctorRecords> findAllByDoctorIsVerifiedTrue();

    List<DoctorRecords> findAllByDoctorIsVerifiedTrueAndSpecialization(MedicalSpecialization specialization);

    @Query("SELECT dr FROM DoctorRecords dr WHERE dr.address.city = :city AND dr.address.state=:state AND dr.address.country=:country")
    List<DoctorRecords> findAllByCity(String city,String state,String country);

}
