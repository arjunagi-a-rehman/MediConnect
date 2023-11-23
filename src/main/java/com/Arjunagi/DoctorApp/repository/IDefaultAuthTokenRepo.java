package com.Arjunagi.DoctorApp.repository;

import com.Arjunagi.DoctorApp.models.authTokens.DefaultAuthToken;
import com.Arjunagi.DoctorApp.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDefaultAuthTokenRepo extends JpaRepository<DefaultAuthToken,Integer> {
    DefaultAuthToken findByValue(String value);

    DefaultAuthToken findByUser(User user);
}
