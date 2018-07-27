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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    /* @GetMapping("/users")
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
    } */

    //change password

    //turn into update
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
                                registerForm.isSeekingKefir(),
                                registerForm.getContactInfo());

        if (!this.isUser(newUser.getUsername())) {
            saveNewUser(newUser);
        } else {
            notificationService.addErrorMessage("Username already exists.");
            return "users/register";
        }
        notificationService.addInfoMessage("Registration successful!");
        return "redirect:/";
    }


    @GetMapping("/users/login")
    public String login(LoginForm loginForm) {
        return "users/login";
    }

    @PostMapping("/users/login")
    public String loginPage(HttpSession httpSession, @Valid LoginForm loginForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            notificationService.addErrorMessage("Please re-enter the login form.");
            return "users/login";
        }

        if (!this.authenticateUser(loginForm.getUsername(), loginForm.getPassword())) {
            notificationService.addErrorMessage("Invalid username or password.");
            return "users/login";
        }

        List<User> result = userRepository.findByUsernameIgnoreCase(loginForm.getUsername());
        User currentUser = result.get(0);
        httpSession.setAttribute("user", currentUser);
        currentUser.setLoggedIn(true);
        notificationService.addInfoMessage("Login successful!");

        return "redirect:/";
    }

    @PostMapping("/users/logout")
    public String logout(HttpSession session ) {
        // I'd use session.invalidate, but re-entering the Spring Security generated password is painful.
        User currentUser = (User) session.getAttribute("user");
        currentUser.setLoggedIn(false);
        session.removeAttribute("user");
        return "redirect:/users/login";
    }

    private boolean authenticateUser(String username, String password) {
        List<User> result = userRepository.findByUsernameIgnoreCase(username);
        return !result.isEmpty() && result.get(0).authenticate(password);
        }

    private boolean isUser(String username) {
        List<User> result = userRepository.findByUsernameIgnoreCase(username);
        return !result.isEmpty();
    }

    private User saveNewUser(User newUser) {
        return userRepository.save(newUser);
    }

    @GetMapping("/users/status")
    public String status(HttpSession session) {
        String currentStatus = "";

        User currentUser = (User) session.getAttribute("user");

        List<User> sharingInZipCode = userRepository.findByZipCodeAndSeekingKefirFalseAndPartnerIdLessThan(currentUser.getZipCode(), 1L);
        List<User> receivingInZipCode = userRepository.findByZipCodeAndSeekingKefirTrueAndPartnerIdLessThan(currentUser.getZipCode(), 1L);

        if (currentUser.isSeekingKefir()) {
            currentStatus += receivingStatus(currentUser, sharingInZipCode, receivingInZipCode);
        } else {
            currentStatus += sharingStatus(currentUser, sharingInZipCode, receivingInZipCode);
        }
        //List<User> sharingInZipCode = userRepository.findByZipCodeAndSeekingKefirFalseOrderByIdAsc(currentUser.getZipCode());
        //List<User> receivingInZipCode = userRepository.findByZipCodeAndSeekingKefirTrueOrderByIdAsc(currentUser.getZipCode());


        session.setAttribute("currentStatus", currentStatus);
        return "users/status";
    }

    private String sharingStatus(User currentUser, List<User> sharingInZipCode, List<User> receivingInZipCode) {
        String currentStatus = "";
        if (receivingInZipCode.isEmpty()) {
           currentStatus += "The queue is empty!";
        } else {

            if (currentUser.getPartnerId() <= 0) {
                User newPartnerUser = receivingInZipCode.get(0);
                currentUser.setPartnerId(newPartnerUser.getId());
                newPartnerUser.setPartnerId(currentUser.getId());
                userRepository.save(currentUser);
                userRepository.save(newPartnerUser);
            }

            Optional<User> optionalUser = userRepository.findById(currentUser.getPartnerId());
            User partnerUser;

            if (optionalUser.isPresent()) {
                partnerUser = optionalUser.get();
            } else {
                return "Something has gone wrong!";
            }
            currentStatus += "There's someone waiting in the queue to receive kefir!"
                    + "\nPlease contact " + partnerUser.getFullName()
                    + " using the following contact information:\n"
                    + partnerUser.getContactInfo();
            }
        return currentStatus;
    }

    private String receivingStatus(User currentUser, List<User> sharingInZipCode, List<User> receivingInZipCode) {
        String currentStatus = "";
        int headCount = 0;
        for (User user : receivingInZipCode) {
            if (user.getId() < currentUser.getId()) {
                headCount++;
            } else if (user.getId().equals(currentUser.getId())) {
                break;
            }
        }
        int peopleAheadOfUser = headCount - sharingInZipCode.size();
        if (peopleAheadOfUser == 1) {
            currentStatus += "There is currently 1 person ahead of you in the queue for your area.";
        } else if (peopleAheadOfUser > 0) {
            currentStatus += "There are currently " + peopleAheadOfUser + " people ahead of you in the queue for your area.";
        } else {

            if (currentUser.getPartnerId() <= 0) {
                User newPartnerUser = sharingInZipCode.get(0);
                currentUser.setPartnerId(newPartnerUser.getId());
                newPartnerUser.setPartnerId(currentUser.getId());
                userRepository.save(currentUser);
                userRepository.save(newPartnerUser);
            }

            Optional<User> optionalUser = userRepository.findById(currentUser.getPartnerId());
            User partnerUser;

            if (optionalUser.isPresent()) {
                partnerUser = optionalUser.get();
            } else {
                return "Something has gone wrong!";
            }

            currentStatus += "It is now your turn to receive kefir!"
            + "\nPlease contact " + partnerUser.getFullName()
            + " using the following contact information:\n"
            + partnerUser.getContactInfo();
        }
       return currentStatus;
    }

}
