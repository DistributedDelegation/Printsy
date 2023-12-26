package cart.repository;

import cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Date;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT COUNT(c) FROM Cart c JOIN Product p ON c.productId = p.productId WHERE p.imageId = ?1")
    Integer checkImagesInCart(Long imageId);

    @Query("SELECT c FROM Cart c WHERE c.userId = ?1")
    List<Cart> checkCartItemsByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.userId = ?1")
    void deleteByUserId(Long userId);



    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Transactional
    @Query("INSERT INTO Cart (userId, productId, expirationTime) VALUES (?1, ?2, ?3)")
    void insertCartItem(Long userId, Long productId, Date expirationTime);

    @Modifying
    @Transactional
    @Query("UPDATE Cart SET expirationTime = ?3 WHERE userId = ?1 AND productId = ?2")
    void updateCartItem(Long userId, Long productId, Date expirationTime);
}
