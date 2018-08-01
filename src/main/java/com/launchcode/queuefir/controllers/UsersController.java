package com.launchcode.queuefir.controllers;

import com.launchcode.queuefir.forms.*;
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

    @GetMapping("/users/update")
    public String update(UpdateForm updateForm) {
        return "users/update";
    }

    @PostMapping("users/update")
    public String updatePage(HttpSession httpSession, @Valid UpdateForm updateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            notificationService.addErrorMessage("Please try to update your details again.");
            return "users/update";
        }

        if (!updateForm.getPassword().equals(updateForm.getConfirmPassword())) {
            notificationService.addErrorMessage("Password and confirmation do not match.");
            return "users/update";
        }

        User currentUser = (User) httpSession.getAttribute("user");

        if (!updateForm.getFullName().isEmpty()) {
            currentUser.setFullName(updateForm.getFullName());
        }

        if (!updateForm.getZipCode().isEmpty()) {
            int newZipCode = Integer.parseInt(updateForm.getZipCode());
            if (newZipCode < 0 || newZipCode > 99950) {
                notificationService.addErrorMessage("Invalid ZIP code provided.");
                return "users/update";
            }
            currentUser.setZipCode(newZipCode);
        }

        if (!updateForm.getFullName().isEmpty()) {
            currentUser.setFullName(updateForm.getFullName());
        }

        if (updateForm.getSeekingKefir() != null && !updateForm.getSeekingKefir().isEmpty()) {
            if (updateForm.getSeekingKefir().equals("receiving")) {
               currentUser.setSeekingKefir(true);
            } else {
                currentUser.setSeekingKefir((false));
            }
        }

        if (!updateForm.getContactInfo().isEmpty()) {
            currentUser.setContactInfo(updateForm.getContactInfo());
        }

        if (!updateForm.getPassword().isEmpty()) {
            currentUser.setPassword(updateForm.getPassword());
        }
        userRepository.save(currentUser);

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
    public String status(HttpSession session, StatusForm statusForm) {
        String currentStatus = "";

        User currentUser = (User) session.getAttribute("user");

        List<User> sharingInZipCode = userRepository.findByZipCodeAndSeekingKefirFalseAndPartnerIdLessThan(currentUser.getZipCode(), 1L);
        List<User> receivingInZipCode = userRepository.findByZipCodeAndSeekingKefirTrueAndPartnerIdLessThan(currentUser.getZipCode(), 1L);

        if (currentUser.isSeekingKefir()) {
            currentStatus += receivingStatus(currentUser, sharingInZipCode, receivingInZipCode);
        } else {
            currentStatus += sharingStatus(currentUser, sharingInZipCode, receivingInZipCode);
        }

        session.setAttribute("currentStatus", currentStatus);
        return "users/status";
    }

    @PostMapping("/users/status")
    public String statusPage(HttpSession httpSession, @Valid StatusForm statusForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            notificationService.addErrorMessage("Please try checking the status again.");
            return "users/status";
        }
        User currentUser = (User) httpSession.getAttribute("user");

        Optional<User> optionalUser = userRepository.findById(currentUser.getPartnerId());
        User partnerUser;

        if (optionalUser.isPresent()) {
            partnerUser = optionalUser.get();
        } else {
            return "Something has gone wrong!";
        }

        if (statusForm.isReceivedKefir()) {
            partnerUser.setPartnerId(0L);
            currentUser.setPartnerId(0L);
            userRepository.save(currentUser);
            userRepository.save(partnerUser);
            if (currentUser.isSeekingKefir()) {
                return "redirect:/users/offboard";
            }
        }
       notificationService.addErrorMessage("Please contact the administrator to resolve.");
       return "redirect:/";
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
                    + "<br>Contact: " + partnerUser.getFullName()
                    + "<br>Contact information: <br>"
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
        if (sharingInZipCode.isEmpty()) {
            currentStatus += "There is currently no one sharing kefir in your area. " +
                    "Please consider becoming the first! " +
                    "<br><a href='https://www.culturesforhealth.com/starter-cultures/kefir-starter-cultures.html'>" +
                    "You can buy grains here!</a>";
        } else if (peopleAheadOfUser == 1) {
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

            currentStatus += "It is now your turn to receive kefir!<br>"
            + "<br>Contact: " + partnerUser.getFullName()
            + "<br>Contact information:<br>"
            + partnerUser.getContactInfo();
        }
       return currentStatus;
    }

    @GetMapping("/users/offboard")
    public String offboard(HttpSession session, OffBoardForm offBoardForm) {
        return "users/offboard";
    }

    @PostMapping("/users/offboard")
    public String offBoardPage(HttpSession httpSession, @Valid OffBoardForm offBoardForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            notificationService.addErrorMessage("An error has occurred. Please try again.");
            return "users/offboard";
        }

        User currentUser = (User) httpSession.getAttribute("user");
        if (offBoardForm.isConvertToSharing()) {
            currentUser.setSeekingKefir(false);
            userRepository.save(currentUser);
            notificationService.addInfoMessage("Your account will now share kefir.");
        } else {
            userRepository.delete(currentUser);
            httpSession.removeAttribute("user");
            notificationService.addInfoMessage("Your account has been deleted.");
        }
        return "redirect:/";
    }
}
