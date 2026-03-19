package drinkshop.service.dep;

import drinkshop.domain.Order;
import drinkshop.domain.Product;

import java.util.List;

public interface ICsvExporter {
    void exportOrders(List<Product> products, List<Order> orders, String path);
}
