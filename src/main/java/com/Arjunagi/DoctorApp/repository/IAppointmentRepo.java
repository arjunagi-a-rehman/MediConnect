package com.Arjunagi.DoctorApp.repository;

import com.Arjunagi.DoctorApp.models.appontment.Appointment;
import com.Arjunagi.DoctorApp.models.appontment.AppointmentStatus;
import com.Arjunagi.DoctorApp.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentRepo extends JpaRepository<Appointment,Integer> {
    List<Appointment> findAllByPatient(User patient);
    List<Appointment> findAllByDoctor(User doctor);
    List<Appointment> findAllByPatientAndStatus(User user, AppointmentStatus status);
    List<Appointment> findAllByDoctorAndStatus(User user, AppointmentStatus status);
    List<Appointment> findAllByStatus(AppointmentStatus status);

    List<Appointment> findAllByPatientAndStatusOrderByScheduledTimeStamp(User user, AppointmentStatus status);
    List<Appointment> findAllByDoctorAndStatusOrderByScheduledTimeStamp(User user, AppointmentStatus status);
    List<Appointment> findAllByStatusOrderByScheduledTimeStamp(AppointmentStatus status);
    List<Appointment> findAllByStatusAndScheduledTimeStampLessThan(AppointmentStatus status, LocalDateTime dateTime);

}
