package com.Arjunagi.DoctorApp.services;

import com.Arjunagi.DoctorApp.models.Address;
import com.Arjunagi.DoctorApp.models.doctorRecord.DoctorRecords;
import com.Arjunagi.DoctorApp.models.doctorRecord.MedicalSpecialization;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.models.dtos.DoctorDataOutPutDTO;
import com.Arjunagi.DoctorApp.models.dtos.VerificationDataDto;
import com.Arjunagi.DoctorApp.models.users.Role;
import com.Arjunagi.DoctorApp.models.users.User;
import com.Arjunagi.DoctorApp.repository.IAddressRepo;
import com.Arjunagi.DoctorApp.repository.IDefaultAuthTokenRepo;
import com.Arjunagi.DoctorApp.repository.IDoctorRecordsRepo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class DoctorRecoredService {
    @Autowired
    UserServices userServices;
    @Autowired
    IDoctorRecordsRepo doctorRecordsRepo;
    @Autowired
    IAddressRepo addressRepo;
    @Autowired
    MailHandler mailHandler;
    @Autowired
    GeoServices geoServices;
    @Autowired
    IDefaultAuthTokenRepo authTokenRepo;
    public String addDocInfoToRecordForVerification(VerificationDataDto verificationDataDto) {
        User doctor = userServices.getUserFromDto(verificationDataDto.getInpDto());
        if (userServices.isValidRole(doctor,  Role.DOCTOR)) {
            DoctorRecords doctorRecords = doctorRecordsRepo.findByDoctor(doctor);
            Address address = verificationDataDto.getHospitalAddress();
            if (address.getId() != null && addressRepo.existsById(address.getId())) {
                address = addressRepo.getReferenceById(address.getId());
            } else {
                addressRepo.save(address);
            }
            doctorRecords.setAddress(address);
            doctorRecords.setExperience(verificationDataDto.getExperience());
            doctorRecords.setConsultationFee(verificationDataDto.getConsultationFee());
            doctorRecords.setLicenseNumber(verificationDataDto.getLicenseNumber());
            doctorRecords.setQualification(verificationDataDto.getQualification());
            doctorRecords.setSpecialization(verificationDataDto.getSpecialization());
            doctorRecords.setTimeRequiredPerPatientInMin(verificationDataDto.getTimeRequiredPerPatientInMin());
            doctorRecords.setEndingHours(verificationDataDto.getEndingHours());
            doctorRecords.setStartingHours(verificationDataDto.getStartingHours());
            doctorRecords.setBreakStartingTime(verificationDataDto.getBreakStartingTime());
            doctorRecords.setBreakEndingTime(verificationDataDto.getBreakEndingTime());
            doctorRecordsRepo.save(doctorRecords);
            if(!doctor.isVerified())
                mailHandler.sendMail(doctor.getEmail(),"Data Uploaded sucessfully","data uploaded sucessfully we are looking forward to work with you!!");
            return "data uploaded sucessfully";
        }
        return "unauthorized access";
    }
    List<DoctorDataOutPutDTO> fromDoctorRecordListToDoctorDataOutPutDto(List<DoctorRecords> records){
        return records.stream().map(doctorRecords -> new DoctorDataOutPutDTO(
                        doctorRecords.getId(),
                        doctorRecords.getDoctor().getName(),
                        doctorRecords.getQualification(),
                        doctorRecords.getSpecialization(),
                        (double)doctorRecords.getConsultationFee(),
                        doctorRecords.getExperience(),
                        doctorRecords.getAddress())).
                collect(Collectors.toList());
    }

    public List<DoctorDataOutPutDTO> getAllVerifiedDoctorsData(String value) {
        User user =authTokenRepo.findByValue(value).getUser();
        if(user!=null){
            return fromDoctorRecordListToDoctorDataOutPutDto(doctorRecordsRepo.findAllByDoctorIsVerifiedTrue());
        }
        return null;
    }

    public List<DoctorDataOutPutDTO> getAllBySpecialization(String value, MedicalSpecialization specialization) {
        User user =authTokenRepo.findByValue(value).getUser();
        if(user!=null){
            return fromDoctorRecordListToDoctorDataOutPutDto(doctorRecordsRepo.findAllByDoctorIsVerifiedTrueAndSpecialization(specialization));
        }
        return null;
    }
    private String filterString(String s){
        StringBuilder sb=new StringBuilder();
        int n=s.length();
        for(int i=0;i<n;i++){
            if(!Character.isAlphabetic(s.charAt(i)))break;
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
    @SneakyThrows
    public List<DoctorDataOutPutDTO> getAllByCity(String value, Double latitude, Double longitude) {
        User user =authTokenRepo.findByValue(value).getUser();
        if(user!=null){
            List<String> add=geoServices.findCity(latitude,longitude);
            return fromDoctorRecordListToDoctorDataOutPutDto(doctorRecordsRepo.findAllByCity(add.get(0),filterString(add.get(1).trim()),add.get(2).trim()));
        }
        return null;
    }
    @SneakyThrows
    public TreeMap<Double, DoctorDataOutPutDTO> getAllDocByDistance(String value, Double latitude, Double longitude) {
        User user =authTokenRepo.findByValue(value).getUser();
        if(user!=null){
            List<String> add=geoServices.findCity(latitude,longitude);
            List<DoctorDataOutPutDTO> doctorDataOutPutDTOS=fromDoctorRecordListToDoctorDataOutPutDto(doctorRecordsRepo.findAllByCity(add.get(0),filterString(add.get(1).trim()),add.get(2).trim()));
            TreeMap<Double,DoctorDataOutPutDTO> doctorRecordByDist=new TreeMap<>();
            doctorDataOutPutDTOS.forEach(record->doctorRecordByDist.put(geoServices.calculateDistance(record.getAddress().getLatitude(),record.getAddress().getLongitude(),latitude,longitude),record));
            return doctorRecordByDist;
        }
        return null;
    }
}
