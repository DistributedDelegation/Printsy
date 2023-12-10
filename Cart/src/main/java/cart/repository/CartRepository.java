package cart.repository;

import cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT COUNT(c) FROM Cart c JOIN Product p ON c.productId = p.productId WHERE p.imageId = ?1")
    Integer checkImagesInCart(Long imageId);

    @Query("SELECT c FROM Cart c WHERE c.userId = ?1")
    List<Cart> checkCartItemsByUserId(Long userId);
}
