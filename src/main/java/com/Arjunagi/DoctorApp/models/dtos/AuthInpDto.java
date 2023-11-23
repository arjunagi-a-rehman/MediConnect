package com.Arjunagi.DoctorApp.models.dtos;

import com.Arjunagi.DoctorApp.models.users.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthInpDto {
    private String email;
    private String tokenValue;
    private Role role;
    private String message;
}
