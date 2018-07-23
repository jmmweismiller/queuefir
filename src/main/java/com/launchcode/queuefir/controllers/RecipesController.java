package com.launchcode.queuefir.controllers;

import com.launchcode.queuefir.models.Recipe;
import com.launchcode.queuefir.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RecipesController {
    
    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/recipes")
    public Iterable<Recipe> findAllRecipes() {
        return recipeRepository.findAll();
    }

    @GetMapping("/recipes/{recipeId}")
    public Optional<Recipe> findRecipeById(@PathVariable Long recipeId) {
        return recipeRepository.findById(recipeId);
    }

    @PostMapping("/recipes")
    public Recipe createNewRecipe(@RequestBody Recipe newRecipe) {
        return recipeRepository.save(newRecipe);
    }

    @PatchMapping("/recipes/{recipeId}")
    public Recipe updateRecipeById(@PathVariable Long recipeId, @RequestBody Recipe recipeRequest){

        Recipe recipeFromDb = recipeRepository.findById(recipeId).get();

        recipeFromDb.setName(recipeRequest.getName());
        recipeFromDb.setBody(recipeRequest.getBody());
        recipeFromDb.setAuthor(recipeRequest.getAuthor());
        recipeFromDb.setDate(recipeRequest.getDate());

        return recipeRepository.save(recipeFromDb);
    }
    @DeleteMapping("recipes/{recipeId}")
    public HttpStatus deleteRecipeById(@PathVariable Long recipeId) {
        recipeRepository.deleteById(recipeId);
        return HttpStatus.OK;
    }

}
