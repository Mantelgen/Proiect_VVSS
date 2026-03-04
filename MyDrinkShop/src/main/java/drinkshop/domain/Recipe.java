package drinkshop.domain;

import java.util.List;

public class Recipe {

    private int id;
    private List<RecipeIngredient> ingrediente;

    public Recipe(int id, List<RecipeIngredient> ingrediente) {
        this.id = id;
        this.ingrediente = ingrediente;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public List<RecipeIngredient> getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(List<RecipeIngredient> ingrediente) {
        this.ingrediente = ingrediente;
    }

    @Override
    public String toString() {
        return "Reteta{" +
                "productId=" + id +
                ", ingrediente=" + ingrediente +
                '}';
    }
}

