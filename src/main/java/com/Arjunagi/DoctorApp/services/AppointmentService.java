package com.Arjunagi.DoctorApp.services;

import com.Arjunagi.DoctorApp.models.appontment.Appointment;
import com.Arjunagi.DoctorApp.models.TimeSlot;
import com.Arjunagi.DoctorApp.models.appontment.AppointmentStatus;
import com.Arjunagi.DoctorApp.models.dtos.AppointmentDto;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.models.users.Role;
import com.Arjunagi.DoctorApp.models.users.User;
import com.Arjunagi.DoctorApp.repository.IAppointmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    IAppointmentRepo appointmentRepo;
    @Autowired
    private UserServices userServices;
    @Autowired
    private MailHandler mailHandler;

    public String bookAppointment(AppointmentDto appointmentDto) {
        User patient=userServices.getUserFromDto(appointmentDto.getAuthInpDto());
        if(userServices.isValidRole(patient, Role.PATIENT)){
            User doctor=userServices.getUserUsingId(appointmentDto.getDocId());
            if(!doctor.getRole().equals(Role.DOCTOR) || !doctor.isVerified())return "invalid doctor";
            TimeSlot timeSlot=userServices.getTimeSlotById(doctor,appointmentDto.getSlotTime());
            Appointment appointment=new Appointment(timeSlot.getStartTime(),timeSlot.getEndTime(),appointmentDto.getSymptoms(),patient,doctor);
            appointmentRepo.save(appointment);
            userServices.removeSlotForDoc(doctor,appointmentDto.getSlotTime());
            String message="appointment booked for "+patient.getName()+" with Dr."+doctor.getName()+" On "+timeSlot.getStartTime();
            mailHandler.sendMail(patient.getName(),"Appointment booked",message);
            return message;
        }
        return "un authorized access";
    }
    List<Appointment> getAllAppointmentsByUser(User user){
        return appointmentRepo.findAllByPatient(user);
    }

    public List<Appointment> getAllForUser(AuthInpDto inpDto) {
        User user=userServices.getUserFromDto(inpDto);
        if(userServices.isValidRole(user,Role.PATIENT)){
            return getAllAppointmentsByUser(user);
        }
        if(userServices.isValidRole(user,Role.DOCTOR)){
            return appointmentRepo.findAllByDoctor(user);
        }
        if(userServices.isValidRole(user,Role.ADMIN)){
            return appointmentRepo.findAll();
        }
        return null;
    }

    public String cancelAppointment(AuthInpDto inpDto, Integer appointmentId) {
        User user=userServices.getUserFromDto(inpDto);
        Appointment appointment=appointmentRepo.findById(appointmentId).orElseThrow();
        if(!appointment.getStatus().equals(AppointmentStatus.BOOKED))return "wrong appointment";
        if(userServices.verifyRoleAndEmailUserInpDto(user,inpDto)){
            String message;
            if(user.getRole().equals(Role.PATIENT)){
                appointment.setStatus(AppointmentStatus.CANCELED_BY_PATIENT);
                 message="appointment at "+appointment.getScheduledTimeStamp()+" with Dr."+appointment.getDoctor().getName()+" canceled sucessfully please feel free to reach out to us for any health related issues. Your Health is our top priority";
            }else if(user.getRole().equals(Role.DOCTOR)){
                appointment.setStatus(AppointmentStatus.CANCELED_BY_DOCTOR);
                message="appointment at "+appointment.getScheduledTimeStamp()+" with Dr."+appointment.getDoctor().getName()+" canceled by doctor due to xyz reason please feel free to reschedule appointment from our app else our executive will reach-out to you regarding same";
            }else{
                appointment.setStatus(AppointmentStatus.CANCELED_BY_ADMIN);
                message="appointment at "+appointment.getScheduledTimeStamp()+" with Dr."+appointment.getDoctor().getName()+" canceled by doctor due to xyz reason please feel free to reschedule appointment from our app else our executive will reach-out to you regarding same";
            }
            appointmentRepo.save(appointment);
            if(appointment.getScheduledTimeStamp().isAfter(LocalDateTime.now())){
                userServices.addBackTheSlot(appointment.getDoctor(),appointment.getScheduledTimeStamp());
            }
            mailHandler.sendMail(appointment.getPatient().getEmail(),"your Appointment canceled",message);
            return message;
        }
        return "wong credentials";
    }

    public String addDiagnosticAndPrescription(AuthInpDto inpDto, Integer appointmentId, String diagnostic, String prescription,Double bp,Double sugar) {
        if(userServices.isValidRole(inpDto,Role.DOCTOR)){
            Appointment appointment=appointmentRepo.findById(appointmentId).orElseThrow();
            if(!appointment.getStatus().equals(AppointmentStatus.BOOKED))return "Invalid appointment";
            appointment.setDiagnostics(diagnostic);
            appointment.setPrescription(prescription);
            appointment.setStatus(AppointmentStatus.SUCCESSFUL);
            appointment.setBp(bp);
            appointment.setSugar(sugar);
            appointmentRepo.save(appointment);
            mailHandler.sendMail(appointment.getPatient().getEmail(),"Thanks for visiting us","<b>Summary</b><p>At:"+appointment.getScheduledTimeStamp()+"<br>patient name:"+appointment.getPatient().getName()+"<br>Doctor:"+appointment.getDoctor().getName()+"<br>diagnostics "+appointment.getDiagnostics()+"<br>prescription:"+appointment.getPrescription()+"</p><p>please follow the doctors advice and prescription</p><p>We wish you best health</p>");
            return "sucessfully finished appointment Thanks for your service";
        }
        return "not authorized user";
    }

    public List<Appointment> getAllBookedAppointments(AuthInpDto inpDto,AppointmentStatus status) {
        User user=userServices.getUserFromDto(inpDto);
        if(userServices.isValidRole(user,Role.PATIENT)){
            return appointmentRepo.findAllByPatientAndStatusOrderByScheduledTimeStamp(user,status);
        }
        if(userServices.isValidRole(user,Role.DOCTOR)){
            return appointmentRepo.findAllByDoctorAndStatusOrderByScheduledTimeStamp(user,status);
        }
        if(userServices.isValidRole(user,Role.ADMIN)){
            return appointmentRepo.findAllByStatusOrderByScheduledTimeStamp(status);
        }
        return null;
    }
    @Scheduled(fixedRate = 3600000)
    public void sendNotificationToPatient(){
        appointmentRepo.findAllByStatusAndScheduledTimeStampLessThan(AppointmentStatus.BOOKED,LocalDateTime.now().minusMinutes(40)).stream().forEach(appointment -> mailHandler.sendMail(appointment.getPatient().getEmail(),"Reminder","Your appointment time is less then 40 minutes"));
    }
}
