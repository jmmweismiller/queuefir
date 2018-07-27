package com.launchcode.queuefir.repositories;

import com.launchcode.queuefir.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByUsernameIgnoreCase(String username);
    List<User> findByZipCodeAndSeekingKefirFalseOrderByIdAsc(int zipCode);
    List<User> findByZipCodeAndSeekingKefirTrueOrderByIdAsc(int zipCode);
    List<User> findByZipCodeAndSeekingKefirFalseAndPartnerIdLessThan(int zipCode, Long minimum);
    List<User> findByZipCodeAndSeekingKefirTrueAndPartnerIdLessThan(int zipCode, Long minimum);
}

