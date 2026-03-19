package drinkshop.service.dep;

import drinkshop.domain.Order;
import drinkshop.domain.Product;

import java.util.List;

public interface IReceiptGenerator {
    String generate(Order o, List<Product> products);
    void saveAsCsv(Order o, List<Product> products, String path);
}
