package cart.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "expiration_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

//---------------Getters and Setters-----------------

    public Cart() {  
    }

    public Cart(Long userId, Long productId, Date expirationTime) {
        this.userId = userId;
        this.productId = productId;
        this.expirationTime = expirationTime;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Date getExpirationTime() {
        return expirationTime;
    }
    
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
  
}