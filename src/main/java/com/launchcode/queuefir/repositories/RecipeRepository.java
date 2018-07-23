package com.launchcode.queuefir.repositories;

import com.launchcode.queuefir.models.Recipe;
import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {

}
