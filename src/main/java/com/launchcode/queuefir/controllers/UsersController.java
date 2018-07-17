package com.launchcode.queuefir.controllers;

import com.launchcode.queuefir.models.user;
import com.launchcode.queuefir.repositories.UserRepository;

@RestController
public class UsersController {

@GetMapping("/users")
public ArrayList<User> findAllUsers() {
	return new ArrayList<User>();
}
}

