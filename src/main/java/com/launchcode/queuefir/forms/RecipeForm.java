package com.launchcode.queuefir.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter @Setter @NotNull
public class RecipeForm {

    private String recipeName;

    private String body;

    private String author;

    private LocalDate publishDate;
}
