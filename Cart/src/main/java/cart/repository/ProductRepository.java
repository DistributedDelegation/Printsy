package cart.repository;

import cart.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
//    @Query("SELECT p FROM Product p WHERE p.productId = ?1")
    Optional<Product> findByProductId(Long productId);

    @Query("SELECT p FROM Product p WHERE p.productId NOT IN (SELECT c.product.productId FROM Cart c)")
    List<Product> findProductsNotInCart();
    
}