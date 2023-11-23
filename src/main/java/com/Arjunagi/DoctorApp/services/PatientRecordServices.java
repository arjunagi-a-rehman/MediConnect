package com.Arjunagi.DoctorApp.services;

import com.Arjunagi.DoctorApp.models.BloodGroup;
import com.Arjunagi.DoctorApp.models.PatientRecord;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.models.users.Role;
import com.Arjunagi.DoctorApp.models.users.User;
import com.Arjunagi.DoctorApp.repository.IDefaultAuthTokenRepo;
import com.Arjunagi.DoctorApp.repository.IPatientRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientRecordServices {
    @Autowired
    IPatientRecordRepo patientRecordRepo;
    @Autowired
    UserServices userServices;
    @Autowired
    IDefaultAuthTokenRepo authTokenRepo;

    public PatientRecord getRecord(String value, Integer patientId) {
        User user=authTokenRepo.findByValue(value).getUser();
        if(user!=null){
            if(user.getRole().equals(Role.PATIENT))return patientRecordRepo.findByPatient(user);
            if(patientId==null)return null;
            User patient=userServices.getUserUsingId(patientId);
            if(patient.getRole().equals(Role.PATIENT))return patientRecordRepo.findByPatient(patient);
            return null;
        }
        return null;
    }

    public String addUpdatePatientDetails(AuthInpDto inpDto, Double height, Double weight, BloodGroup bloodGroup, String emergencyContact) {
        if(height==null&&weight==null&&bloodGroup==null&&emergencyContact==null)return "invalid input";
        User user=userServices.getUserFromDto(inpDto);
        if(userServices.isValidRole(user,Role.PATIENT)){
            PatientRecord patientRecord=patientRecordRepo.findByPatient(user);
            if(height!=null)patientRecord.setHeight(height);
            if(weight!=null)patientRecord.setWeight(weight);
            if(bloodGroup!=null)patientRecord.setBloodGroup(bloodGroup);
            if(emergencyContact!=null)patientRecord.setEmergencyContact(emergencyContact);
            patientRecordRepo.save(patientRecord);
            return "data updated sucessfully";
        }
        return "un-autherized access";
    }
}
