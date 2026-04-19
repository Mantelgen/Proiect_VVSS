package drinkshop.service;

import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceIntUnitTest {
    @InjectMocks
    private ProductService service;

    @Mock
    private Repository<Integer, Product> productRepository;
    @Mock
    private Validator<Product> validator;

    @Test
    void Test1() {
        var p = new Product(1, "nume", 12.0, "cat", "tip");

        service.addProduct(p);

        // Doesn't throw exception
        assert true;
        Mockito.verify(productRepository, times(1)).save(p);
        Mockito.verify(validator, times(1)).validate(p);
    }

    @Test
    void Test2() {
        var p = new Product(0, "nume valid", -44, "cat", "tip");

        Mockito.doThrow(new ValidationException("preț invalid")).when(validator).validate(p);

        assertThrowsExactly(ValidationException.class, () -> service.addProduct(p));
        Mockito.verify(validator, times(1)).validate(p);
        Mockito.verify(productRepository, never()).save(p);
    }
}

@ExtendWith(MockitoExtension.class)
class ProductServiceIntSVTest {
    @InjectMocks
    private ProductService service;

    @Mock
    private Repository<Integer, Product> productRepository;
    @Spy
    private ProductValidator validator;

    @Test
    void Test1() {
        var p = new Product(1, "nume", 12.0, "cat", "tip");

        service.addProduct(p);

        // Doesn't throw exception
        assert true;
        Mockito.verify(productRepository, times(1)).save(p);
        Mockito.verify(validator, times(1)).validate(p);
    }

    @Test
    void Test2() {
        var p = new Product(0, "nume valid", -44, "cat", "tip");

        assertThrowsExactly(ValidationException.class, () -> service.addProduct(p));

        Mockito.verify(validator, times(1)).validate(p);
        Mockito.verify(productRepository, never()).save(p);
    }
}

@ExtendWith(MockitoExtension.class)
class ProductServiceIntSVRTest {
    private ProductService service;
    private File backingFile;
    private FileProductRepository productRepository;
    @Spy
    private ProductValidator validator;

    @BeforeEach
    void setUp() throws Exception {
        backingFile = File.createTempFile("int-test", ".txt");
        var discard = backingFile.createNewFile();
        productRepository = spy(new FileProductRepository(backingFile.getPath()));
        service = new ProductService(productRepository, validator);
    }

    @AfterEach
    void tearDown() {
        var deleted = backingFile.delete();
    }

    @Test
    void Test1() {
        var p = new Product(1, "nume", 12.0, "cat", "tip");

        service.addProduct(p);

        // Doesn't throw exception
        assert true;
        Mockito.verify(productRepository, times(1)).save(p);
        Mockito.verify(validator, times(1)).validate(p);

        var products = service.getAllProducts();
        assertEquals(1, products.size());

        var product = products.get(0);
        assertEquals("nume", product.getNume());
        assertEquals(12, product.getPret());
        assertEquals("cat", product.getCategorie());
        assertEquals("tip", product.getTip());
    }

    @Test
    void Test2() {
        var p = new Product(0, "nume valid", -44, "cat", "tip");

        assertThrowsExactly(ValidationException.class, () -> service.addProduct(p));

        Mockito.verify(validator, times(1)).validate(p);
        Mockito.verify(productRepository, never()).save(p);

        assertEquals(0, service.getAllProducts().size());
    }
}