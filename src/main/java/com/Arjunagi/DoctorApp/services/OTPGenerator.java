package com.Arjunagi.DoctorApp.services;

import java.util.Random;

abstract class OTPGenerator {

    public static String generateOTP(int length) {
        // Define the range of characters for the OTP (0-9)
        String characters = "0123456789";
        // Initialize a random number generator
        Random random = new Random();
        StringBuilder otp = new StringBuilder(length);
        // Generate the OTP by randomly selecting characters from the defined range
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            otp.append(randomChar);
        }
        return otp.toString();
    }
}

