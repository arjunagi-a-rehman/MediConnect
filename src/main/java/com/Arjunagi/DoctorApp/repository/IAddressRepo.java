package com.Arjunagi.DoctorApp.repository;

import com.Arjunagi.DoctorApp.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAddressRepo extends JpaRepository<Address,Integer> {
}
