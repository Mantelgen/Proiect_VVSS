package drinkshop.service.validator;

import drinkshop.domain.RecipeIngredient;
import drinkshop.domain.Recipe;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RetetaValidator implements Validator<Recipe> {

    @Override
    public void validate(Recipe recipe) {

        AtomicReference<String> errors = new AtomicReference<>("");

        if (recipe.getId() <= 0)
            errors.accumulateAndGet("Recipe ID invalid!\n", String::concat);

        List<RecipeIngredient> ingrediente = recipe.getIngrediente();
        if (ingrediente == null || ingrediente.isEmpty())
            errors.accumulateAndGet("Ingrediente empty!\n", String::concat);
        if(ingrediente != null)
             ingrediente.stream()
                .filter(entry -> entry.getCantitate() <= 0)
                .forEach(entry ->
                    errors.accumulateAndGet("[" + entry.getDenumire() + "]"+ "cantitate negativa sau zero", String::concat)
                );

        if (!errors.get().isEmpty())
            throw new ValidationException(errors.get());
    }
}
