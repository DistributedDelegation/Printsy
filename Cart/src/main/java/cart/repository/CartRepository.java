package cart.repository;

import cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT COUNT(*) FROM Cart WHERE image_id = ?1")
    Integer checkImagesInCart(Long imageId);

    @Query("SELECT * FROM Cart WHERE user_id = ?1")
    List<Cart> findCartItemsByUserId(Long userId);
}
