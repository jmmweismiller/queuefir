package com.launchcode.queuefir.repositories;

import com.launchcode.queuefir.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}

