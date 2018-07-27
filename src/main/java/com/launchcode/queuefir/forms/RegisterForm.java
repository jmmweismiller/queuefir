package com.launchcode.queuefir.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter @Setter
public class RegisterForm {

    @Size(min=2, max=30, message = "Username size should be in the range [2...30]")
    private String username;

    @Size(min=1, max=50)
    private String password;

    @Size(min=1, max=50)
    private String confirmPassword;

    @Size(min=1, max=50)
    private String fullName;

    private boolean seekingKefir;

    @Min(0) @Max(99950)
    private int zipCode;

    @Size(min=1, max=500)
    private String contactInfo;
}
