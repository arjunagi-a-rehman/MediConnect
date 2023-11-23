package com.Arjunagi.DoctorApp.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {
    private AuthInpDto authInpDto;
    private String symptoms;
    private LocalDateTime slotTime;
    private Integer docId;

}
