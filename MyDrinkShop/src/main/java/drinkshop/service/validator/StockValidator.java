package drinkshop.service.validator;

import drinkshop.domain.Stock;

public class StockValidator implements Validator<Stock> {

    @Override
    public void validate(Stock stock) {

        String errors = "";

        if (stock.getId() <= 0)
            errors += "ID invalid!\n";

        if (stock.getIngredient() == null || stock.getIngredient().isBlank())
            errors += "Ingredient invalid!\n";

        if (stock.getCantitate() < 0)
            errors += "Cantitate negativa!\n";

        if (stock.getStocMinim() < 0)
            errors += "Stoc minim negativ!\n";

        if (stock.getCantitate() < stock.getStocMinim())
            errors += "Cantitatea este sub stocul minim!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}