package cart.repository;

import cart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT * FROM Product WHERE product_id = ?1")
    Optional<Product> findByProductId(Long productId);

    @Query("SELECT * FROM Product")
    List<Product> findAllProducts();
}
