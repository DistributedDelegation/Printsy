package product;

import product.model.Product;
import product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testCreateAndFindProduct() {
        Product product = new Product();
        product.setImageId(1L);
        product.setStockId(1L);
        product.setPrice(100);

        product = productRepository.save(product);
        assertNotNull(product.getProductId());

        Product foundProduct = productRepository.findById(product.getProductId()).orElse(null);
        assertNotNull(foundProduct);
        assertEquals(1L, foundProduct.getImageId());
        assertEquals(1L, foundProduct.getStockId());
        assertEquals(100, foundProduct.getPrice());
    }
}
