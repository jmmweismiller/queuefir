package com.launchcode.queuefir.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter @Setter
public class UpdateForm {

    private String password;

    private String confirmPassword;

    private String fullName;

    private String seekingKefir;

    private String zipCode;

    private String contactInfo;

    private boolean deletingAccount;
}
