package com.Arjunagi.DoctorApp.repository;

import com.Arjunagi.DoctorApp.models.users.Role;
import com.Arjunagi.DoctorApp.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserRepo extends JpaRepository<User,Integer> {
    User findFirstByEmail(String email);

    List<User> findByRoleAndIsVerified(Role user, boolean b);
}
