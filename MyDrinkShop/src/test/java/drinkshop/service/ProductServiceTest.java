package drinkshop.service;

import drinkshop.domain.Product;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTest {

    private ProductService service;
    private InMemoryProductRepository productRepo;

    @BeforeEach
    void setUp() {
        productRepo = new InMemoryProductRepository();
        service = new ProductService(productRepo);
    }

    @Test
    @Order(1)
    @Tag("ECP")
    @DisplayName("P233-1 ECP valid - update only name")
    void modifyValidProductTest_ECP() {
        // Arrange
        int id = 10;
        Product initial = seedProduct(id, "Latte", 15.0, "Coffee", "Milk");

        // Act
        assertDoesNotThrow(() -> service.updateProduct(id, "Latte Nou", initial.getPret(), initial.getCategorie(), initial.getTip()));

        // Assert
        Product updated = service.findById(id);
        assertAll(
                () -> assertEquals("Latte Nou", updated.getNume()),
                () -> assertEquals(initial.getPret(), updated.getPret()),
                () -> assertEquals(initial.getCategorie(), updated.getCategorie()),
                () -> assertEquals(initial.getTip(), updated.getTip())
        );
    }

    @Test
    @Order(2)
    @Tag("ECP")
    @DisplayName("P233-3 ECP invalid - empty name")
    void modifyInvalidProductTest_ECP() {
        // Arrange
        int id = 11;
        Product initial = seedProduct(id, "Espresso", 12.0, "Coffee", "Simple");

        double currentPrice = initial.getPret();
        String currentCategory = initial.getCategorie();
        String currentType = initial.getTip();

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.updateProduct(id, "", currentPrice, currentCategory, currentType));

        // Assert
        Product unchanged = service.findById(id);
        assertAll(
                () -> assertTrue(ex.getMessage().contains("Numele nu poate fi gol!")),
                () -> assertEquals(initial.getNume(), unchanged.getNume()),
                () -> assertEquals(initial.getPret(), unchanged.getPret()),
                () -> assertEquals(initial.getCategorie(), unchanged.getCategorie()),
                () -> assertEquals(initial.getTip(), unchanged.getTip())
        );
    }

    @Test
    @Order(3)
    @Tag("BVA")
    @DisplayName("P233-4 BVA valid - price at 0.5")
    void modifyValidProductTest_BVA() {
        // Arrange
        int id = 12;
        Product initial = seedProduct(id, "Green Tea", 9.0, "Tea", "Water");

        // Act
        assertDoesNotThrow(() -> service.updateProduct(id, initial.getNume(), 0.5, initial.getCategorie(), initial.getTip()));

        // Assert
        Product updated = service.findById(id);
        assertAll(
                () -> assertEquals(initial.getNume(), updated.getNume()),
                () -> assertEquals(0.5, updated.getPret()),
                () -> assertEquals(initial.getCategorie(), updated.getCategorie()),
                () -> assertEquals(initial.getTip(), updated.getTip())
        );
    }

    @Test
    @Order(4)
    @Tag("BVA")
    @DisplayName("P233-5 BVA invalid - price at 0")
    void modifyInvalidProductTest_BVA() {
        // Arrange
        int id = 13;
        Product initial = seedProduct(id, "Orange Juice", 14.0, "Juice", "Fresh");

        String currentName = initial.getNume();
        String currentCategory = initial.getCategorie();
        String currentType = initial.getTip();

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.updateProduct(id, currentName, 0.0, currentCategory, currentType));

        // Assert
        Product unchanged = service.findById(id);
        assertAll(
                () -> assertTrue(ex.getMessage().contains("Pret invalid!")),
                () -> assertEquals(initial.getNume(), unchanged.getNume()),
                () -> assertEquals(initial.getPret(), unchanged.getPret()),
                () -> assertEquals(initial.getCategorie(), unchanged.getCategorie()),
                () -> assertEquals(initial.getTip(), unchanged.getTip())
        );
    }

    @Test
    @Order(5)
    @Tag("ECP")
    @DisplayName("P233-6 ECP valid - update only price")
    void modifyValidProductTest_ECP2() {
        // Arrange
        int id = 14;
        Product initial = seedProduct(id, "Bubble Tea", 18.0, "Tea", "Boba");

        // Act
        assertDoesNotThrow(() -> service.updateProduct(id, initial.getNume(), 20.0, initial.getCategorie(), initial.getTip()));

        // Assert
        Product updated = service.findById(id);
        assertAll(
                () -> assertEquals(initial.getNume(), updated.getNume()),
                () -> assertEquals(20.0, updated.getPret()),
                () -> assertEquals(initial.getCategorie(), updated.getCategorie()),
                () -> assertEquals(initial.getTip(), updated.getTip())
        );
    }

    @Test
    @Order(6)
    @Tag("ECP")
    @DisplayName("P233-7 ECP invalid - negative price")
    void modifyInvalidProductTest_ECP2() {
        // Arrange
        int id = 15;
        Product initial = seedProduct(id, "Iced Coffee", 16.0, "Coffee", "Cold");

        String currentName = initial.getNume();
        String currentCategory = initial.getCategorie();
        String currentType = initial.getTip();

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.updateProduct(id, currentName, -1.0, currentCategory, currentType));

        // Assert
        Product unchanged = service.findById(id);
        assertAll(
                () -> assertTrue(ex.getMessage().contains("Pret invalid!")),
                () -> assertEquals(initial.getNume(), unchanged.getNume()),
                () -> assertEquals(initial.getPret(), unchanged.getPret()),
                () -> assertEquals(initial.getCategorie(), unchanged.getCategorie()),
                () -> assertEquals(initial.getTip(), unchanged.getTip())
        );
    }

    @Test
    @Order(7)
    @Tag("BVA")
    @DisplayName("P233-8 BVA valid - price at MAXDOUBLE")
    void modifyValidProductTest_BVA2() {
        // Arrange
        int id = 16;
        Product initial = seedProduct(id, "Smoothie", 19.0, "Cold", "Fruit");

        // Act
        assertDoesNotThrow(() -> service.updateProduct(id, initial.getNume(), Double.MAX_VALUE, initial.getCategorie(), initial.getTip()));

        // Assert
        Product updated = service.findById(id);
        assertAll(
                () -> assertEquals(initial.getNume(), updated.getNume()),
                () -> assertEquals(Double.MAX_VALUE, updated.getPret()),
                () -> assertEquals(initial.getCategorie(), updated.getCategorie()),
                () -> assertEquals(initial.getTip(), updated.getTip())
        );
    }

    @Test
    @Order(8)
    @Tag("BVA")
    @DisplayName("P233-9 BVA invalid - price at MAXDOUBLE+1")
    void modifyInvalidProductTest_BVA2() {
        // Arrange
        int id = 17;
        Product initial = seedProduct(id, "Cappuccino", 17.0, "Coffee", "Milk");
        // MAXDOUBLE+1 overflows to +Infinity in IEEE-754 doubles.
        double aboveMax = Math.nextUp(Double.MAX_VALUE);

        String currentName = initial.getNume();
        String currentCategory = initial.getCategorie();
        String currentType = initial.getTip();

        // Act
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.updateProduct(id, currentName, aboveMax, currentCategory, currentType));

        // Assert
        Product unchanged = service.findById(id);
        assertAll(
                () -> assertTrue(ex.getMessage().contains("Pret invalid!")),
                () -> assertEquals(initial.getNume(), unchanged.getNume()),
                () -> assertEquals(initial.getPret(), unchanged.getPret()),
                () -> assertEquals(initial.getCategorie(), unchanged.getCategorie()),
                () -> assertEquals(initial.getTip(), unchanged.getTip())
        );
    }

    private Product seedProduct(int id, String name, double price, String category, String type) {
        Product product = new Product(id, name, price, category, type);
        productRepo.save(product);
        return product;
    }

    private static class InMemoryProductRepository extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }
}

