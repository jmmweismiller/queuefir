package com.launchcode.queuefir.repositories;

import com.launchcode.queuefir.models.Recipe;
import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    List<Recipe> findByAuthorId(Long authorId);
    Optional<Recipe> findByRecipeName(String recipeName);
}
