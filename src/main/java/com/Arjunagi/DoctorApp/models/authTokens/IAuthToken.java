package com.Arjunagi.DoctorApp.models.authTokens;

import java.time.LocalDateTime;

public interface IAuthToken {
    Integer getId();
    void setId(Integer id);
    String getValue();
    void setValue(String value);
    LocalDateTime getCreationDateTime();
    void setCreationDateTime(LocalDateTime localDateTime);
}
