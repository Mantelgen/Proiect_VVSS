package drinkshop.service;

import drinkshop.domain.Recipe;
import drinkshop.repository.Repository;
import drinkshop.service.validator.RetetaValidator;

import java.util.List;

public class RecipeService {

    private final Repository<Integer, Recipe> retetaRepo;
    private final RetetaValidator validator = new RetetaValidator();

    public RecipeService(Repository<Integer, Recipe> retetaRepo) {
        this.retetaRepo = retetaRepo;
    }

    public void addReteta(Recipe r) {
        validator.validate(r);
        retetaRepo.save(r);
    }

    public void updateReteta(Recipe r) {
        retetaRepo.update(r);
    }

    public void deleteReteta(int id) {
        retetaRepo.delete(id);
    }

    public Recipe findById(int id) {
        return retetaRepo.findOne(id);
    }

    public List<Recipe> getAll() {
        return retetaRepo.findAll();
    }
}