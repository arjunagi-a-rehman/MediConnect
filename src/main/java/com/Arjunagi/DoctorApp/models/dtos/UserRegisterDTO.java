package com.Arjunagi.DoctorApp.models.dtos;

import com.Arjunagi.DoctorApp.models.BloodGroup;
import com.Arjunagi.DoctorApp.models.users.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    private String name;
    @Email
    private String email;
    private String number;
    private String password;
}
