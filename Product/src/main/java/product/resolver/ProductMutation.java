package product.resolver;

import product.model.Product;
import product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProductMutation {

    private final ProductService productService;

    @Autowired
    public ProductMutation(ProductService productService) {
        this.productService = productService;
    }

    @MutationMapping
    public Product createProduct(@Argument Long imageId, @Argument Long stockId, @Argument Integer price) {
        return productService.createProduct(imageId, stockId, price);
    }

    @MutationMapping
    public String deleteProduct(@Argument Long productId) {
        productService.deleteProductById(productId);
        return "Product deleted successfully";
    }
}
