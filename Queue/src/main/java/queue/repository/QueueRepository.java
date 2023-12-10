package queue.repository;

import cart.model.Cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface QueueRepository extends JpaRepository<Cart, Long> {

    //@Modifying
    //@Transactional
    //@Query("DELETE FROM Cart c WHERE c.userId = ?1")
    //void deleteByUserId(Long userId);

}