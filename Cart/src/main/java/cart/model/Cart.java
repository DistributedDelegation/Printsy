package cart.model;

import cart.dto.CartResult;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Date;

import cart.dto.TransactionInput;

@Entity
@Table(name = "Cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id") // foreign key
    private Product product;

    @Column(name = "expiration_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant expirationTime;

//---------------Getters and Setters-----------------

    public Cart() {
        this.expirationTime = Instant.now();
    }

    public Cart(Long userId, Product product, Instant expirationTime) {
        this.userId = userId;
        this.product = product;
        this.expirationTime = Instant.now();
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
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Instant getExpirationTime() {
        return expirationTime;
    }
    
    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }


    public TransactionInput toTransactionInput(){
        return new TransactionInput(this.userId, this.product.getProductId(), this.product.getImageId());
    }

    public CartResult toCartResult(){
        return new CartResult(this.userId, this.product.getProductId(), this.expirationTime);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", product=" + product +
                ", expirationTime=" + expirationTime +
                '}';
    }
}