package com.Arjunagi.DoctorApp.services;

import com.Arjunagi.DoctorApp.models.PatientRecord;
import com.Arjunagi.DoctorApp.models.TimeSlot;
import com.Arjunagi.DoctorApp.models.appontment.Appointment;
import com.Arjunagi.DoctorApp.models.doctorRecord.DoctorRecords;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.models.dtos.LogInDto;
import com.Arjunagi.DoctorApp.models.dtos.UserRegisterDTO;
import com.Arjunagi.DoctorApp.models.users.Role;
import com.Arjunagi.DoctorApp.models.users.User;
import com.Arjunagi.DoctorApp.repository.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServices {
    private Set<String> resetRequest = new HashSet<>();
    @Autowired
    private IUserRepo userRepo;
    @Autowired
    private HashMap<String, String> userOTOMap;
    @Autowired
    private HashSet<String> OTPSet;
    @Autowired
    private MailHandler mailHandler;
    @Autowired
    private DefaultAuthTokenServices authTokenServices;
    @Autowired
    private IAppointmentRepo appointmentRepo;
    @Autowired
    private IPatientRecordRepo patientRecordRepo;
    @Autowired
    private IDoctorRecordsRepo doctorRecordsRepo;
    @Autowired
    private IAddressRepo addressRepo;

    private HashMap<User, TreeMap<LocalDateTime, TimeSlot>> doctorSlotsMap = new HashMap<>();
    private LocalDateTime dateTime=LocalDateTime.now();


    @Async
    public void sendOTP(String email) {
        String otp = OTPGenerator.generateOTP(5);
        while (OTPSet.contains(otp)) {
            otp = OTPGenerator.generateOTP(5);
        }
        OTPSet.add(otp);
        if (userOTOMap.get(email) != null) {
            OTPSet.remove(userOTOMap.get(email));
        }
        userOTOMap.put(email, otp);
        String message = "<h2>Thanks for Joining Our App</h2>" +
                "<p>We are looking forward to provide best services</p>" +
                "Your OTP is:<b>" + otp;
        mailHandler.sendMail(email, "Don't share OTP", message);
    }

    public boolean verifyOTP(String email, String userOtp) {
        String otp = userOTOMap.get(email);
        if (otp != null && otp.equals(userOtp)) {
            User user = userRepo.findFirstByEmail(email);
            if (!user.getRole().equals(Role.PATIENT)) return false;
            user.setVerified(true);
            userRepo.save(user);
            return true;
        }
        return false;
    }


    @SneakyThrows
    public String addPatient(UserRegisterDTO userRegisterDTO) {
        String enPass = PasswordEncryptor.encrypt(userRegisterDTO.getPassword());
        User user = new User(userRegisterDTO.getName(), userRegisterDTO.getEmail(), userRegisterDTO.getNumber(), enPass, Role.PATIENT);
        userRepo.save(user);
        PatientRecord patientRecord = new PatientRecord(user);
        patientRecordRepo.save(patientRecord);
        sendOTP(user.getEmail());
        return "please verify your email OTP has been sent";
    }

    @SneakyThrows
    public AuthInpDto loginUser(LogInDto logInDto) {
        User user = userRepo.findFirstByEmail(logInDto.getEmail());
        AuthInpDto authInpDto = new AuthInpDto();
        if (user == null) {
            authInpDto.setMessage("wrong credentials");
            return authInpDto;
        }

        if (!PasswordEncryptor.encrypt(logInDto.getPassword()).equals(user.getPassword())) {
            authInpDto.setMessage("wrong credentials");
            return authInpDto;
        }
        if (!user.isVerified() && user.getRole().equals(Role.PATIENT)) {
            sendOTP(logInDto.getEmail());
            authInpDto.setMessage("please first verify your account");
            return authInpDto;
        }
        authInpDto.setMessage("loggedIn successfully");
        authInpDto.setRole(user.getRole());
        authInpDto.setEmail(user.getEmail());
        authInpDto.setTokenValue(authTokenServices.getAuthToken(user));

        return authInpDto;
    }

    @SneakyThrows
    public String addDoctor(UserRegisterDTO userRegisterDTO) {
        User user = new User(userRegisterDTO.getName(), userRegisterDTO.getEmail(), userRegisterDTO.getNumber(), PasswordEncryptor.encrypt(userRegisterDTO.getPassword()), Role.DOCTOR);
        DoctorRecords doctorRecords = new DoctorRecords(user);
        userRepo.save(user);
        doctorRecordsRepo.save(doctorRecords);
        mailHandler.sendMail(user.getEmail(),"Thanks for choosing us","Thanks for registration we are looking forward to have a great doctors please upload the required data for verification ");
        return "Thanks for registration we are looking forward to have a great doctors please upload the required data for verification ";
    }

    private boolean isAdminMail(String mail) {
        String domain = "@admin.com";
        int dl = domain.length();
        int ml = mail.length();
        if (ml <= dl) return false;
        while (dl-- > 0) {
            if (mail.charAt(--ml) != domain.charAt(dl)) return false;
        }
        return true;
    }

    @SneakyThrows
    public String addAdmin(UserRegisterDTO userRegisterDTO) {
        if (!isAdminMail(userRegisterDTO.getEmail())) return "wrong email";
        User user = new User(userRegisterDTO.getName(), userRegisterDTO.getEmail(), userRegisterDTO.getNumber(), PasswordEncryptor.encrypt(userRegisterDTO.getPassword()), Role.ADMIN);
        user.setVerified(true);
        userRepo.save(user);
        return "registered sucessfully";
    }

    User getUserFromDto(AuthInpDto inpDto) {
        return authTokenServices.getUserForValidToken(inpDto.getTokenValue());
    }

    boolean isValidRole(User user, Role role) {
        return  user.getRole().equals(role);
    }

    boolean isValidRole(AuthInpDto inpDto, Role role) {
        User user = getUserFromDto(inpDto);
        return isValidRole(user, role);
    }


    public List<User> getAllUsers(String value) {
        User user = authTokenServices.getUserForValidToken(value);
        if (isValidRole(user, Role.ADMIN)) return userRepo.findAll();
        return null;
    }

    public List<User> getAllUnVerifiedDocs(String value) {
        User user = authTokenServices.getUserForValidToken(value);
        if (user.getRole().equals(Role.ADMIN)) {
            return userRepo.findByRoleAndIsVerified(Role.DOCTOR, false);
        }
        return null;
    }

    public String verifyTheDoc(AuthInpDto inpDto, Integer docId) {
        User user = getUserFromDto(inpDto);
        if (isValidRole(user,  Role.ADMIN)) {
            User doc = userRepo.findById(docId).orElseThrow();
            if (!doc.getRole().equals(Role.DOCTOR) || doc.isVerified()) return "wrong doc";
            doc.setVerified(true);
            userRepo.save(doc);
            mailHandler.sendMail(doc.getEmail(),"Congratulations","Your account has been verified sucessfully");
            return "verified sucessfully";
        }
        return "not authorized";
    }

    public List<User> getAllVerifiedDocs(String value) {
        User user = authTokenServices.getUserForValidToken(value);
        if (isValidRole(user, Role.ADMIN) || isValidRole(user, Role.PATIENT)) {
            return userRepo.findByRoleAndIsVerified(Role.DOCTOR, true);
        }
        return null;
    }

    boolean verifyRoleAndEmailUserInpDto(User user, AuthInpDto inpDto) {
        return user != null && user.getEmail().equals(inpDto.getEmail()) && user.getRole().equals(inpDto.getRole());
    }

    public String logOut(AuthInpDto inpDto) {
        if (authTokenServices.deleteToken(inpDto)) return "logged out sucessfully";
        return "your not authorized";
    }

    @SneakyThrows
    public String resetPassword(String email, String pass) {
        User user = userRepo.findFirstByEmail(email);
        if (!user.getRole().equals(Role.PATIENT)) return "not authorized";
        if (!resetRequest.contains(email)) return "please first pass the request";
        if (!user.isVerified()) return "unverified user";
        user.setPassword(PasswordEncryptor.encrypt(pass));
        userRepo.save(user);
        authTokenServices.deleteTokenForUser(user);
        return "password reset successful";
    }

    public String sendEmailOtp(String email) {
        User user = userRepo.findFirstByEmail(email);
        if (user == null) return "not registered";
        if (!user.isVerified()) return "unverified user please contact customer service";
        sendOTP(email);
        resetRequest.add(email);
        user.setVerified(false);
        userRepo.save(user);
        return "mail has been sent please confirm the OTP";
    }

    public TreeMap<LocalDateTime, TimeSlot> generateTimeSlots(Set<LocalDateTime> booked, int intervalMinutes, LocalTime workStartTime, LocalTime workEndTime, LocalTime breakStarting, LocalTime breakEnding, int daysToAdd) {
        TreeMap<LocalDateTime, TimeSlot> timeSlots = new TreeMap<>();
        int id = 1;
        LocalDateTime currentDateTime = LocalDateTime.now().isAfter(dateTime) ? LocalDateTime.now() : dateTime;
        while (daysToAdd > 0) {
            LocalDateTime currentStartTime = currentDateTime.withHour(workStartTime.getHour()).withMinute(workStartTime.getMinute());
            while (!currentStartTime.toLocalDate().getDayOfWeek().equals(DayOfWeek.SATURDAY) &&
                    !currentStartTime.toLocalDate().getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                    currentStartTime.isBefore(currentDateTime.withHour(workEndTime.getHour()).withMinute(workEndTime.getMinute()))) {

                LocalDateTime currentEndTime = currentStartTime.plusMinutes(intervalMinutes);

                if (!booked.contains(currentStartTime) &&( (currentStartTime.toLocalTime().isBefore(breakStarting) || currentStartTime.toLocalTime().isAfter(breakEnding)))) {
                    timeSlots.put(currentStartTime, new TimeSlot(id++, currentStartTime, currentEndTime));
                }

                currentStartTime = currentEndTime;
            }
            currentDateTime = currentDateTime.plusDays(1);
            daysToAdd--;
        }

        return timeSlots;
    }



    public String AssignTimeSlotsToDocs(AuthInpDto inpDto, Integer docId, LocalTime startTime, LocalTime endTime,LocalTime breakStarting,LocalTime breakEnding) {
        User user = getUserFromDto(inpDto);
        if ( user.getRole().equals(Role.ADMIN)) {
            User doc = userRepo.findById(docId).orElseThrow();
            if (!doc.getRole().equals(Role.DOCTOR)) return "wrong doc";
            Set<LocalDateTime> booked = appointmentRepo.findAllByDoctor(doc).stream().map(Appointment::getScheduledTimeStamp).collect(Collectors.toSet());
            doctorSlotsMap.put(doc, generateTimeSlots(booked, 20, startTime, endTime,breakStarting,breakEnding,7));
            return "slots added sucessfully";
        }
        return "unauthorized access";
    }

    private void removeOutdatedSlots(User doctor) {
        TreeMap<LocalDateTime, TimeSlot> slots = doctorSlotsMap.get(doctor);
        while (slots.firstKey().isBefore(LocalDateTime.now())) {
            slots.pollFirstEntry();
        }
    }


    public TreeMap<LocalDateTime, TimeSlot> getAllAvailableSlots(String value, Integer docId) {
        User user = authTokenServices.getUserForValidToken(value);



        User doc = userRepo.findById(docId).orElse(null);

        if (doc == null || !doc.getRole().equals(Role.DOCTOR) || !doc.isVerified()) {
            return null; // Doctor not found or not eligible
        }

        if (doctorSlotsMap.get(doc) == null) {
            DoctorRecords doctorRecords = doctorRecordsRepo.findByDoctor(doc);
            Set<LocalDateTime> booked = appointmentRepo.findAllByDoctor(doc)
                    .stream()
                    .map(Appointment::getScheduledTimeStamp)
                    .collect(Collectors.toSet());

            TreeMap<LocalDateTime, TimeSlot> timeSlots = generateTimeSlots(
                    booked,
                    doctorRecords.getTimeRequiredPerPatientInMin(),
                    LocalTime.now().isAfter(doctorRecords.getStartingHours())
                            ? LocalTime.now()
                            : doctorRecords.getStartingHours(),
                    doctorRecords.getEndingHours(),
                    doctorRecords.getBreakStartingTime(),
                    doctorRecords.getBreakEndingTime(),
                    7
            );

            doctorSlotsMap.put(doc, timeSlots);
        } else {
            removeOutdatedSlots(doc);
        }

        return doctorSlotsMap.get(doc);
    }


    TimeSlot getTimeSlotById(User doctor, LocalDateTime startTime) {
        return doctorSlotsMap.get(doctor).get(startTime);
    }

    public User getUserUsingId(Integer id) {
        return userRepo.findById(id).orElseThrow();
    }

    public void removeSlotForDoc(User doc, LocalDateTime slotId) {
        doctorSlotsMap.get(doc).remove(slotId);
    }

    public void addBackTheSlot(User doctor, LocalDateTime scheduledTimeStamp) {
        doctorSlotsMap.get(doctor).put(scheduledTimeStamp, new TimeSlot(1, scheduledTimeStamp, scheduledTimeStamp.plusMinutes(20)));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void addNewSlotsDaily(){
        dateTime=dateTime.plusDays(1);
        for(User doctor:userRepo.findByRoleAndIsVerified(Role.DOCTOR,true)){
            DoctorRecords doctorRecords=doctorRecordsRepo.findByDoctor(doctor);
            Set<LocalDateTime> booked = appointmentRepo.findAllByDoctor(doctor).stream().map(Appointment::getScheduledTimeStamp).collect(Collectors.toSet());
            if(doctorSlotsMap.get(doctor)!=null)
                generateTimeSlots(booked,doctorRecords.getTimeRequiredPerPatientInMin(),doctorRecords.getStartingHours(),doctorRecords.getEndingHours(),doctorRecords.getBreakStartingTime(),doctorRecords.getBreakEndingTime(),1).forEach((key, value)->doctorSlotsMap.get(doctor).merge(key, value, (v1, v2) -> v2));
            else {
                dateTime=dateTime.minusDays(1);
                doctorSlotsMap.put(doctor, generateTimeSlots(booked, doctorRecords.getTimeRequiredPerPatientInMin(), doctorRecords.getStartingHours(), doctorRecords.getEndingHours(), doctorRecords.getBreakStartingTime(), doctorRecords.getBreakEndingTime(), 7));
            }
        }
    }

    public String removeAllSlotsBetween(AuthInpDto inpDto, LocalDateTime startTime, LocalDateTime endTime) {
        User user = authTokenServices.getUserForValidToken(inpDto.getTokenValue());
        if(isValidRole(user,Role.DOCTOR)){
            TreeMap<LocalDateTime,TimeSlot> map=doctorSlotsMap.get(user);

            for(LocalDateTime dateTime1:map.subMap(startTime,endTime).keySet()){
                map.remove(dateTime1);
            }
            return "removed the slots sucessfully";
        }
        return "un-autherized access";
    }

}
