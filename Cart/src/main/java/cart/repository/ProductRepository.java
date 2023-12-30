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

    @Query("SELECT p FROM Product p WHERE p.id NOT IN (SELECT c.product.id FROM Cart c)")
    List<Product> findProductsNotInCart();

//    @Query("SELECT p FROM Product p JOIN Cart c ON p.productId = c.productId WHERE c.userId = ?1")
//    List<Product> checkCartProductsByUserId(Long userId);

}