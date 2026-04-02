package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.AbstractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest {

    private OrderService orderService;
    private InMemoryOrderRepository orderRepo;
    private InMemoryProductRepository productRepo;

    @BeforeEach
    void setUp() {
        orderRepo = new InMemoryOrderRepository();
        productRepo = new InMemoryProductRepository();
        orderService = new OrderService(orderRepo, productRepo);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @Tag("F02_TC01")
    @DisplayName("F02_TC01 - Null order should return 0€")
    void computeTotal_nullOrder_returns0() {
        // Arrange
        Order order = null;

        // Act
        double result = orderService.computeTotal(order);

        // Assert
        assertEquals(0.0, result);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @Tag("F02_TC02")
    @DisplayName("F02_TC02 - Empty order should return 0€")
    void computeTotal_emptyOrder_returns0() {
        // Arrange
        Order order = new Order(1);

        // Act
        double result = orderService.computeTotal(order);

        // Assert
        assertEquals(0.0, result);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @Tag("F02_TC03")
    @DisplayName("F02_TC03 - Non-existent product should throw error")
    void computeTotal_nonExistentProduct_throwsException() {
        // Arrange
        Order order = new Order(1);
        Product nonExistentProduct = new Product(999, "NonExistent", 10.0, "Drink", "Type");
        OrderItem item = new OrderItem(nonExistentProduct, 1);
        order.addItem(item);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.computeTotal(order));
        assertTrue(exception.getMessage().contains("necunoscuți"));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @Tag("F02_TC04")
    @DisplayName("F02_TC04 - Negative quantity should throw error")
    void computeTotal_negativeQuantity_throwsException() {
        // Arrange
        Order order = new Order(1);
        Product product = new Product(1, "Lapte", 10.0, "Drink", "Type");
        productRepo.save(product);
        OrderItem item = new OrderItem(product, -1);
        order.addItem(item);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.computeTotal(order));
        assertTrue(exception.getMessage().contains("negative"));
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @Tag("F02_TC05")
    @DisplayName("F02_TC05 - Negative product price should throw error")
    void computeTotal_negativePrice_throwsException() {
        // Arrange
        Order order = new Order(1);
        Product product = new Product(1, "Cafea", -10.0, "Drink", "Type");
        productRepo.save(product);
        OrderItem item = new OrderItem(product, 2);
        order.addItem(item);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.computeTotal(order));
        assertTrue(exception.getMessage().contains("negativ"));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @Tag("F02_TC06")
    @DisplayName("F02_TC06 - Multiple valid items should compute correct total: 40€")
    void computeTotal_multipleValidItems_returns40() {
        // Arrange
        Order order = new Order(1);
        Product apa = new Product(1, "Apa", 12.0, "Drink", "Type");
        Product lapte = new Product(2, "Lapte", 14.0, "Drink", "Type");
        productRepo.save(apa);
        productRepo.save(lapte);

        OrderItem item1 = new OrderItem(apa, 1);      // 12 * 1 = 12
        OrderItem item2 = new OrderItem(lapte, 2);    // 14 * 2 = 28
        order.addItem(item1);
        order.addItem(item2);

        // Act
        double result = orderService.computeTotal(order);

        // Assert
        assertEquals(40.0, result);
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    @Tag("F02_TC07")
    @DisplayName("F02_TC07 - Single valid item should compute correct total: 4€")
    void computeTotal_singleValidItem_returns4() {
        // Arrange
        Order order = new Order(1);
        Product miere = new Product(1, "Miere", 2.0, "Drink", "Type");
        productRepo.save(miere);
        OrderItem item = new OrderItem(miere, 2);     // 2 * 2 = 4
        order.addItem(item);

        // Act
        double result = orderService.computeTotal(order);

        // Assert
        assertEquals(4.0, result);
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    @Tag("F02_TC08")
    @DisplayName("F02_TC08 - Zero price product should throw error")
    void computeTotal_zeroPriceProduct_throwsException() {
        // Arrange
        Order order = new Order(1);
        Product apa = new Product(1, "Apa", 2.0, "Drink", "Type");
        Product portocale = new Product(2, "Portocale", 0.0, "Drink", "Type");
        productRepo.save(apa);
        productRepo.save(portocale);

        OrderItem item1 = new OrderItem(apa, 1);
        OrderItem item2 = new OrderItem(portocale, 1);
        order.addItem(item1);
        order.addItem(item2);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.computeTotal(order));
        assertTrue(exception.getMessage().contains("negativ"));
    }

    private static class InMemoryOrderRepository extends AbstractRepository<Integer, Order> {
        @Override
        protected Integer getId(Order entity) {
            return entity.getId();
        }
    }

    private static class InMemoryProductRepository extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }
}
