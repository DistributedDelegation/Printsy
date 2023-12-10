package cart.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Cart")
public class CartItem {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long cartID;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "image_id")
    private Long image_id;

    @Column(name = "stock_id")
    private Long stock_id;

    @Column(name = "price")
    private Long price;

    @Column(name = "expiration_time")
    private Date expiration_time;

    public Long getCartID() {
        return cartID;
    }

    public void setCartID(Long cartID) {
        this.cartID = cartID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getImage_id() {
        return image_id;
    }

    public void setImage_id(Long image_id) {
        this.image_id = image_id;
    }

    public Long getStock_id() {
        return stock_id;
    }

    public void setStock_id(Long stock_id) {
        this.stock_id = stock_id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Date getExpiration_time() {
        return expiration_time;
    }

    public void setExpiration_time(Date expiration_time) {
        this.expiration_time = expiration_time;
    }

    //
//    private String itemId; // itemId is the primary key for single items
//    private String itemGenerationId; // itemGenerationId limits at 10 max.
//    private String itemImgName; // itemImgName from image generation
//    private String itemName; // itemName from image generation
//    private double itemPrice; // itemPrice from from product selection
//    private int quantity; // quantity of the item in the cart;
//                          // needs to be checked against GenerationId's table stockQuantity
//    private String itemDescription; // itemDescription from image generation
//    private ProductType itemProductType; // itemProductType from product selection
//    private Color itemColour; // itemColour from product selection
//    private Size itemSize; // itemSize from product selection
//
//
//    // Constants
//    public enum ProductType {
//        CUP, SHIRT, HOODIE, HAT, STICKER
//    }
//    public enum Size {
//        XS, S, M, L, XL, XXL, NA; // 'NA' for not applicable
//    }
//    public enum Color {
//        RED, GREEN, BLUE, BLACK, WHITE;
//    }
//
//    // Constructor
//    public CartItem(String itemId, String itemImgName, String itemName, double itemPrice, int quantity,
//                    String itemDescription, String itemProductType, String itemColour, String itemSize) {
//        this.itemId = itemId;
//        this.itemImgName = itemImgName;
//        this.itemName = itemName;
//        this.itemPrice = itemPrice;
//        setQuantity(quantity); // This will validate the quantity
//        this.itemDescription = itemDescription;
//        this.itemProductType = itemProductType;
//        this.itemColour = itemColour;
//        this.itemSize = itemProductType;
//    }
//
//    // Getter and setter (with validation) for itemId
//    public String getItemId() {
//        return itemId;
//    }
//
//    public void setItemId(String itemId) {
//        this.itemId = itemId;
//    }
//
//    public void setItemProductType(ProductType productType) {
//        this.itemProductType = productType;
//
//        // Automatically set itemSize to "NA" if the product type doesn't have a size
//        switch(this.itemProductType) {
//            case CUP:
//            case HAT:
//            case STICKER:
//                this.itemSize = "NA";
//                break;
//            default:
//                break;
//        }
//
//    }
//
//
//    // Add the rest of the getters and setters here...
}