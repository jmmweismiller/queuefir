package com.launchcode.queuefir.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class StatusForm {

    @NotNull
    private boolean transferredKefir;

}
