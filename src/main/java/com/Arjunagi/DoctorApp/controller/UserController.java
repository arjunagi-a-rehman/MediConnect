package com.Arjunagi.DoctorApp.controller;

import com.Arjunagi.DoctorApp.models.TimeSlot;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.models.dtos.LogInDto;
import com.Arjunagi.DoctorApp.models.dtos.UserRegisterDTO;
import com.Arjunagi.DoctorApp.models.dtos.VerificationDataDto;
import com.Arjunagi.DoctorApp.models.users.User;
import com.Arjunagi.DoctorApp.services.GeoServices;
import com.Arjunagi.DoctorApp.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.TreeMap;

@RestController
public class UserController {
    @Autowired
    UserServices userServices;
    @Autowired
    GeoServices geoServices;


    //----------register users ----------------------------------------------------------------------

    @Operation(summary = "patient registration",description = "end point will take all the user details and register. Once the form is submitted user must verify itself The OTP will come to the mail and patient will use that otp with 'user/verification' end point(check verification section)",tags = "register")
    @PostMapping("/user/patient")
    public String addPatient(@RequestBody UserRegisterDTO userRegisterDTO){
        return userServices.addPatient(userRegisterDTO);
    }//after registration of the patient wl  get verification otp on mail the patient need to verify by using verify api "user/verification"
    @Operation(summary = "Doctor registration",description = "Doctor will be verified by admin only",tags = "register")
    @PostMapping("/user/doctor")
    public String addDoctor(@RequestBody UserRegisterDTO userRegisterDTO){
        return userServices.addDoctor(userRegisterDTO);
    }// the doctors account can only be verified by the admin
    @Operation(summary = "Admin registration",description = "admin don't need any verification but admin mail must end with @admin.com",tags = "register")
    @PostMapping("/user/admin")
    public String addAdmin(@RequestBody UserRegisterDTO userRegisterDTO){
        return userServices.addAdmin(userRegisterDTO);
    }//admin no need of verification but email must be end with @admin.com


    //------------------------------------------------------------------------

    //---------------Login------------------------------------------------------------------------


    @PostMapping("/user/login")
    public AuthInpDto loginUser(@RequestBody LogInDto logInDto){
        return userServices.loginUser(logInDto);
    }

    // The login page will be same for all the users
    // If patient is not verified then mail will be sent with the otp user need to verify the otp
    // on successful login user will receive the dto with auth token else dto with message

    //----------------------------------------------------------------------------

    //-----------------------  verification ---------------------------------------------------------------

    @PutMapping("user/verification")
    public String verifyOTP(@RequestParam String email,@RequestParam String otp){

        if(userServices.verifyOTP(email,otp))
            return "thank your verified";
        return "wrong otp";
    }  // patient will put otp from the mail



    @PutMapping("user/doc/id/{docId}/verify")
    public String verifyTheDoc(@RequestBody AuthInpDto inpDto,@PathVariable Integer docId){
        return userServices.verifyTheDoc(inpDto,docId);
    }// only admin can verify the doctor



    //----------------------------------------------------------------------------------------

    //------------- Password reset-------------------------------------

    @PostMapping("password/reset/user/verify/mail")
    public String sendEmailOtp(String email){
        return userServices.sendEmailOtp(email);
    }// patient will get the otp on mail first user need to verify self

    // will use the verify api

    @PutMapping("user/forgot/password")
    public String resetPassword(String email,String pass){
        return userServices.resetPassword(email,pass);
    }//now user will send the new password

    //--------------------------------------------------------


    @GetMapping("{value}/users")
    public List<User> getAllUsers(@PathVariable String value){
        return userServices.getAllUsers(value);
    }
    @GetMapping("/users/doctors/unverified/{value}")
    public List<User> getAllUnVerifiedDocs(@PathVariable String value){
        return userServices.getAllUnVerifiedDocs(value);
    }
    @GetMapping("{value}/users/docs")
    public List<User> getAllVerifiedDocs(@PathVariable String value){
        return userServices.getAllVerifiedDocs(value);
    }



    //-----logout--------------------------------------------------------------------------------------

    @DeleteMapping("/user/logout")
    public String logOut(@RequestBody AuthInpDto inpDto){
        return userServices.logOut(inpDto);
    }
    // logout page will be same for everyone

    //--------------------------------------------------------------------------------------------------
    //---------Assign time slots to the doctors---------------------------------------------------------
    @PostMapping("{value}/admin/doctor/time/slots")
    public String AssignTimeSlotsToDocs(@PathVariable AuthInpDto inpDto, @RequestParam Integer docId, @RequestParam LocalTime startTime, @RequestParam LocalTime endTime,@RequestParam LocalTime breakStarting,@RequestParam LocalTime breakEnding){
        return userServices.AssignTimeSlotsToDocs(inpDto,docId,startTime,endTime,breakStarting,breakEnding);
    }
    @GetMapping("{value}/doctor/{docId}/slots")
    public TreeMap<LocalDateTime,TimeSlot> getAllAvailableSlots(@PathVariable String value , @PathVariable Integer docId){
        return userServices.getAllAvailableSlots(value,docId);
    }

    @DeleteMapping("slots/doctor")
    public String removeAllSlotsBetween(@RequestBody AuthInpDto inpDto,@RequestParam LocalDateTime startTime,@RequestParam LocalDateTime endTime){
        return userServices.removeAllSlotsBetween(inpDto,startTime,endTime);
    }
}