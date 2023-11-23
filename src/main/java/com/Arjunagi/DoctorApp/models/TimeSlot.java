package com.Arjunagi.DoctorApp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
