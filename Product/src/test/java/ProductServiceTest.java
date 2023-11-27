package product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import product.service.ProductService;
import product.model.Product;
import product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    public Product savedProduct;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        savedProduct = new Product();
        savedProduct.setProductId(1L); // Set a fixed ID
        savedProduct.setImageId(1L);
        savedProduct.setStockId(1L);
        savedProduct.setPrice(100);

        // When save is called with any Product, return savedProduct with the fixed ID
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        // When findById is called with the saved product's ID, return the saved product
        when(productRepository.findById(savedProduct.getProductId())).thenReturn(Optional.of(savedProduct));
        // Return true for existsById for the saved product's ID
        when(productRepository.existsById(savedProduct.getProductId())).thenReturn(true);
    }


    @Test
    public void testCreateProduct() {
        Long imageId = 1L;
        Long stockId = 1L;
        Integer price = 100;

        Product createdProduct = productService.createProduct(imageId, stockId, price);

        assertNotNull(createdProduct);
        assertEquals(1L, createdProduct.getProductId()); // Assuming the ID is set to 1
        assertEquals(imageId, createdProduct.getImageId());
        assertEquals(stockId, createdProduct.getStockId());
        assertEquals(price, createdProduct.getPrice());
    }

    @Test
    public void testGetProductById() {
        Long productId = 1L;
        Optional<Product> mockProduct = Optional.of(new Product());
        mockProduct.get().setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(mockProduct);

        Optional<Product> foundProduct = productService.getProductById(productId);

        assertTrue(foundProduct.isPresent());
        assertEquals(productId, foundProduct.get().getProductId());
    }

    @Test
    public void testDeleteProductById() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        assertDoesNotThrow(() -> productService.deleteProductById(productId));
        verify(productRepository, times(1)).deleteById(productId);
    }
}
