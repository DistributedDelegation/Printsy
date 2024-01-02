package cart.repository;

import cart.model.Cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Date;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Integer countByProductImageId(String imageId);

    List<Cart> findAllByUserId(Long userId);

    // Method to find all expired carts
    List<Cart> findByExpirationTimeBefore(Instant currentTime);

    boolean existsByUserIdAndProductProductId(Long userId, Long productId);

    Optional<Cart> findByUserIdAndProductProductId(Long userId, Long productId);

    @Query("SELECT DISTINCT c.userId FROM Cart c")
    List<Long> findDistinctUserIds();

    Cart findTopByUserId(Long userId);

//    @Modifying
//    @Transactional
//    @Query("INSERT INTO Cart (userId, productId, expirationTime) VALUES (?1, ?2, ?3)")
//    void insertCartItem(Long userId, Long productId, Date expirationTime);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Cart SET expirationTime = ?3 WHERE userId = ?1 AND productId = ?2")
//    void updateCartItem(Long userId, Long productId, Date expirationTime);
}
