package service.cart;

// Organises the item information
public class CartItem {

    private String itemId; // itemId is the primary key for single items
    private String itemGenerationId; // itemGenerationId limits at 10 max.
    private String itemImgName; // itemImgName from image generation
    private String itemName; // itemName from image generation
    private double itemPrice; // itemPrice from from product selection
    private int quantity; // quantity of the item in the cart;
                          // needs to be checked against GenerationId's table stockQuantity
    private String itemDescription; // itemDescription from image generation
    private ProductType itemProductType; // itemProductType from product selection
    private Color itemColour; // itemColour from product selection
    private Size itemSize; // itemSize from product selection


    // Constants
    public enum ProductType {
        CUP, SHIRT, HOODIE, HAT, STICKER
    }
    public enum Size {
        XS, S, M, L, XL, XXL, NA; // 'NA' for not applicable
    }
    public enum Color {
        RED, GREEN, BLUE, BLACK, WHITE;
    }

    // Constructor
    public CartItem(String itemId, String itemImgName, String itemName, double itemPrice, int quantity,
                    String itemDescription, String itemProductType, String itemColour, String itemSize) {
        this.itemId = itemId;
        this.itemImgName = itemImgName;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        setQuantity(quantity); // This will validate the quantity
        this.itemDescription = itemDescription;
        this.itemProductType = itemProductType;
        this.itemColour = itemColour;
        this.itemSize = itemProductType;
    }

    // Getter and setter (with validation) for itemId
    public String getItemId() { 
        return itemId; 
    }

    public void setItemId(String itemId) { 
        this.itemId = itemId; 
    }

    public void setItemProductType(ProductType productType) {
        this.itemProductType = productType;
    
        // Automatically set itemSize to "NA" if the product type doesn't have a size
        switch(this.itemProductType) {
            case CUP:
            case HAT:
            case STICKER:
                this.itemSize = "NA";
                break;
            default:
                break;
        }
    
    }
    

    // Add the rest of the getters and setters here...
}