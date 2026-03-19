package drinkshop.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductTest {

    Product product;

    @BeforeEach
    void setUp() {
        product =new Product(100, "Limonada", 10.0, "Juice", "Water Based");
    }

    @AfterEach
    void tearDown() {
        product = null;
    }

    @Test
    void getId() {
        assert 100 == product.getId();
    }

    @Test
    void getNume() {
        assert "Limonada".equals(product.getNume());
    }

    @Test
    void getPret() {
        assert 10.0 == product.getPret();
    }

    @Test
    void getCategorie() {
        assert "Juice".equals(product.getCategorie());
    }

    @Test
    void setCategorie() {
        product.setCategorie("Smoothie");
        assert "Smoothie".equals(product.getCategorie());
    }

    @Test
    void getTip() {
        assert "Water Based".equals(product.getTip());
    }

    @Test
    void setTip() {
        product.setTip("Basic");
        assert "Basic".equals(product.getTip());
    }

    @Test
    void setNume() {
        product.setNume("newLimonada");
        assert "newLimonada".equals(product.getNume());
    }

    @Test
    void setPret() {
        product.setPret(10.05);
        assert 10.05 == product.getPret();
    }

    @Test
    void testToString() {
        System.out.println(product.toString());
        assert "Limonada (Juice, Water Based) - 10.0 lei".equals(product.toString());
    }
}