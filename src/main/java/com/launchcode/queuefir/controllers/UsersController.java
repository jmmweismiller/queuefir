package com.launchcode.queuefir.controllers;

import com.launchcode.queuefir.forms.LoginForm;
import com.launchcode.queuefir.forms.RegisterForm;
import com.launchcode.queuefir.models.User;
import com.launchcode.queuefir.repositories.UserRepository;
import com.launchcode.queuefir.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/users")
    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{userId}")
    public Optional<User> findUserById(@PathVariable Long userId) {
        return userRepository.findById(userId);
    }

    @PostMapping("/users")
    public User createNewUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }

    @PatchMapping("/users/{userId}")
    public User updateUserById(@PathVariable Long userId, @RequestBody User userRequest) {

        User userFromDb = userRepository.findById(userId).get();

        userFromDb.setUsername(userRequest.getUsername());
        userFromDb.setPassword(userRequest.getPassword());
        userFromDb.setFullName(userRequest.getFullName());
        userFromDb.setZipCode(userRequest.getZipCode());

        return userRepository.save(userFromDb);
    }

    @DeleteMapping("users/{userId}")
    public HttpStatus deleteUserById(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return HttpStatus.OK;
    }

    @GetMapping("/users/register")
    public String register(RegisterForm registerForm) {

        return "users/register";
    }

    @PostMapping("/users/register")
    public String registerPage(@Valid RegisterForm registerForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            notificationService.addErrorMessage("Please re-enter the registration form.");
            return "users/register";
        }
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            notificationService.addErrorMessage("Password and confirmation do not match.");
            return "users/register";
        }

        User newUser = new User(registerForm.getUsername(),
                                registerForm.getPassword(),
                                registerForm.getFullName(),
                                registerForm.getZipCode(),
                                registerForm.isSeekingKefir());

        if (!this.isUser(newUser.getUsername())) {
            newUser = saveNewUser(newUser);
        } else {
            notificationService.addErrorMessage("Username already exists.");
            return "users/register";
        }

        // newUser = saveNewUser(newUser);

        notificationService.addInfoMessage("Registration successful!");
        return "redirect:/";
    }


    @GetMapping("/users/login")
    public String login(LoginForm loginForm) {
        return "users/login";
    }

    @PostMapping("/users/login")
    public String loginPage(@Valid LoginForm loginForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            notificationService.addErrorMessage("Please re-enter the login form.");
            return "users/login";
        }

        if (!this.authenticateUser(loginForm.getUsername(), loginForm.getPassword())) {
            notificationService.addErrorMessage("Invalid username or password.");
            return "users/login";
        }

        notificationService.addInfoMessage("Login successful!");
        return "redirect:/";
    }

    private boolean authenticateUser(String username, String password) {
        List<User> result = userRepository.findByUsernameIgnoreCase(username);
        return !result.isEmpty() && result.get(0).authenticate(password);
        }

    private boolean isUser(String username) {
        List<User> result = userRepository.findByUsernameIgnoreCase(username);
        return !result.isEmpty();
    }

    public User saveNewUser(User newUser) {
        return userRepository.save(newUser);
    }
}
