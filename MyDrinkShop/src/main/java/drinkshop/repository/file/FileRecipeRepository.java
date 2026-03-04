package drinkshop.repository.file;

import drinkshop.domain.RecipeIngredient;
import drinkshop.domain.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileRecipeRepository
        extends FileAbstractRepository<Integer, Recipe> {

    public FileRecipeRepository(String fileName) {
        super(fileName);
        loadFromFile();
    }

    @Override
    protected Integer getId(Recipe entity) {
        return entity.getId();
    }

    @Override
    protected Recipe extractEntity(String line) {

        String[] elems = line.split(",");

        int productId = Integer.parseInt(elems[0]);
        List<RecipeIngredient> ingrediente = new ArrayList<>();
        int index=1;
        while (index<elems.length) {
            String ingredientTotal= elems[index++];
            String[] ingredientSeparat = ingredientTotal.split(":");
            String ingredientName = ingredientSeparat[0];
            Double ingredientQuantity = Double.parseDouble(ingredientSeparat[1]);
            ingrediente.add(new RecipeIngredient(ingredientName, ingredientQuantity));
        }
        return new Recipe(productId, ingrediente);
    }

    @Override
    protected String createEntityAsString(Recipe entity) {
        String ingrediente = entity.getIngrediente().stream()
                        .map(entry -> entry.getDenumire() + ":" + entry.getCantitate())
                        .collect(Collectors.joining(","));
        return entity.getId() + "," +
                ingrediente;
    }
}
