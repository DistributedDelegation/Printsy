package cart.repository;

import cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.imageId = ?1")
    Long checkImagesInCart(Long imageId);
    
}
