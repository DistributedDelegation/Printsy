package product.service;

import product.model.Product;
import product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ProductService {

    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long productId) {
        LOGGER.info("Fetching product with ID: " + productId);
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            LOGGER.info("Found product: " + product.get());
        } else {
            LOGGER.warning("No product found with ID: " + productId);
        }
        return product;
    }    

    public Product createProduct(Long imageId, Long stockId, Integer price) {
        LOGGER.info("Creating product with imageId: " + imageId + ", stockId: " + stockId + ", price: " + price);
        Product newProduct = new Product();
        newProduct.setImageId(imageId);
        newProduct.setStockId(stockId);
        newProduct.setPrice(price);
    
        Product savedProduct = productRepository.save(newProduct);
        LOGGER.info("Product saved with ID: " + savedProduct.getProductId());
        return savedProduct;
    }    

    public void deleteProductById(Long productId) {
        LOGGER.info("Deleting product with ID: " + productId);
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            LOGGER.info("Product deleted successfully");
        } else {
            LOGGER.warning("Attempted to delete a product that does not exist with ID: " + productId);
            throw new RuntimeException("Product with ID " + productId + " not found");
        }
    }    

}