package com.Arjunagi.DoctorApp.repository;

import com.Arjunagi.DoctorApp.models.users.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;

@Repository
public class OTPRepo {
    @Bean
    HashMap<String,String> userOTPMap(){
        return new HashMap<String,String>();
    }
    @Bean
    HashSet<String> OTPSet(){
        return new HashSet<String>();
    }
}
