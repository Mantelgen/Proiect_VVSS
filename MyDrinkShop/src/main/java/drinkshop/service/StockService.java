package drinkshop.service;

import drinkshop.domain.RecipeIngredient;
import drinkshop.domain.Recipe;
import drinkshop.domain.Stock;
import drinkshop.repository.Repository;
import drinkshop.service.validator.StockValidator;

import java.util.List;

public class StockService {

    private final Repository<Integer, Stock> stocRepo;
    private final StockValidator validator = new StockValidator();

    public StockService(Repository<Integer, Stock> stocRepo) {
        this.stocRepo = stocRepo;
    }

    public List<Stock> getAll() {
        return stocRepo.findAll();
    }

    public void add(Stock s) {
        validator.validate(s);
        stocRepo.save(s);
    }

    public void update(Stock s) {
        validator.validate(s);
        stocRepo.update(s);
    }

    public void delete(int id) {
        stocRepo.delete(id);
    }

    public boolean areSuficient(Recipe recipe) {
        List<RecipeIngredient> ingredienteNecesare = recipe.getIngrediente();

        for (RecipeIngredient e : ingredienteNecesare) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            double disponibil = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .mapToDouble(Stock::getCantitate)
                    .sum();

            if (disponibil < necesar) {
                return false;
            }
        }
        return true;
    }

    public void consuma(Recipe recipe) {
        if (!areSuficient(recipe)) {
            throw new IllegalStateException("Stoc insuficient pentru rețeta.");
        }

        for (RecipeIngredient e : recipe.getIngrediente()) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            List<Stock> ingredienteStock = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .toList();

            double ramas = necesar;

            for (Stock s : ingredienteStock) {
                if (ramas <= 0) break;

                double deScazut = Math.min(s.getCantitate(), ramas);
                s.setCantitate((s.getCantitate() - deScazut));
                ramas -= deScazut;

                stocRepo.update(s);
            }
        }
    }
}