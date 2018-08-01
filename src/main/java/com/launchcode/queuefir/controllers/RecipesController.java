package com.launchcode.queuefir.controllers;

import com.launchcode.queuefir.forms.RecipeForm;
import com.launchcode.queuefir.models.Recipe;
import com.launchcode.queuefir.models.User;
import com.launchcode.queuefir.repositories.RecipeRepository;
import com.launchcode.queuefir.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class RecipesController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/recipes/index")
    public String recipes(HttpSession httpSession) {
        String recipeString = "";
        Iterable<Recipe> allRecipes = recipeRepository.findAll();
        for (Recipe recipe : allRecipes) {
            recipeString += "<br>Name: " + recipe.getRecipeName()
                    + "<br>Body: " + recipe.getBody()
                    + "<br>Author: " + recipe.getAuthor()
                    + "<br>Published: " + recipe.getPublishDate()
                    + "<br>";
        }
        httpSession.setAttribute("recipeString", recipeString);
        return "recipes/index";
    }

    @GetMapping("/recipes/submit")
    public String submitRecipe(HttpSession httpSession, RecipeForm recipeForm) {
        return "recipes/submit";
    }

    @PostMapping("/recipes/submit")
    public String submitRecipePage(HttpSession httpSession, @Valid RecipeForm recipeForm, BindingResult bindingResult) {
        User currentUser = (User) httpSession.getAttribute("user");
        if (currentUser != null && currentUser.isLoggedIn()) {
            Recipe newRecipe = new Recipe(
                    recipeForm.getRecipeName(),
                    recipeForm.getBody(),
                    currentUser.getFullName(),
                    currentUser.getId(),
                    LocalDate.now());
            recipeRepository.save(newRecipe);
            notificationService.addInfoMessage("Recipe added!");
        }
        return "redirect:/recipes/index";
    }

    @GetMapping("/recipes/choose")
    public String chooseRecipe(HttpSession httpSession, String chosenRecipeName) {
        User currentUser = (User) httpSession.getAttribute("user");
        List<Recipe> authoredRecipes = recipeRepository.findByAuthorId(currentUser.getId());
        httpSession.setAttribute("authoredRecipes", authoredRecipes);
        return "recipes/choose";
    }

    @PostMapping("/recipes/choose")
    public String chooseRecipePage(HttpSession httpSession, String chosenRecipeName) {
       Optional<Recipe> chosen = recipeRepository.findByRecipeName(chosenRecipeName);
       Recipe chosenRecipe = null;
       if (chosen.isPresent()) {
           chosenRecipe = chosen.get();
       }
       httpSession.setAttribute("chosenRecipe", chosenRecipe);
       return "redirect:/recipes/update";
    }

    @GetMapping("/recipes/update")
    public String updateRecipeSelection(HttpSession httpSession, RecipeForm recipeForm, BindingResult bindingResult) {
        return "recipes/update";
    }

    @PostMapping("/recipes/update")
    public String updateRecipePage(HttpSession httpSession, @Valid RecipeForm recipeForm, BindingResult bindingResult) {
        User currentUser = (User) httpSession.getAttribute("user");
        if (currentUser != null && currentUser.isLoggedIn()) {
            Recipe recipeToUpdate = (Recipe) httpSession.getAttribute("chosenRecipe");
            recipeToUpdate.setRecipeName(recipeForm.getRecipeName());
            recipeToUpdate.setBody(recipeForm.getBody());
            recipeToUpdate.setPublishDate(LocalDate.now());
            recipeRepository.save(recipeToUpdate);
            notificationService.addInfoMessage("Recipe updated!");
        }
        return "redirect:/recipes/index";
    }

    @PostMapping("/recipes/delete")
    public String logout(HttpSession session) {
        Recipe recipeToDelete = (Recipe) session.getAttribute("chosenRecipe");
        notificationService.addInfoMessage("Deleted " + recipeToDelete.getRecipeName() + "!");
        recipeRepository.delete(recipeToDelete);
        session.removeAttribute("chosenRecipe");
        return "redirect:/recipes/update";
    }
}

