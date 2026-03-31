package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;

import java.util.List;

public class OrderService {

    private final Repository<Integer, Order> orderRepo;
    private final Repository<Integer, Product> productRepo;

    public OrderService(Repository<Integer, Order> orderRepo, Repository<Integer, Product> productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;

    }

    public void addOrder(Order o) {
        orderRepo.save(o);
    }

    public void updateOrder(Order o) {
        orderRepo.update(o);
    }

    public void deleteOrder(int id) {
        orderRepo.delete(id);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order findById(int id) {
        return orderRepo.findOne(id);
    }

    public double computeTotal(Order o) {
        if (o == null)
            return 0;

        double sum = 0;
        for (var item : o.getItems()) {
            var p = productRepo.findOne(item.getProduct().getId());
            if (p == null)
                throw new RuntimeException("Comanda conține itemni necunoscuți");

            if (item.getQuantity() <= 0)
                throw new RuntimeException("Produsul apare în comandă de un număr negative de ori");
            if (p.getPret() <= 0)
                throw new RuntimeException("Produsul are un preț negativ");

            sum += p.getPret() * item.getQuantity();
        }

        return sum;
    }

    public void addItem(Order o, OrderItem item) {
        o.addItem(item);
        orderRepo.update(o);
    }

    public void removeItem(Order o, OrderItem item) {
        o.removeItem(item);
        orderRepo.update(o);
    }
}