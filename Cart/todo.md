CartService: 


public void handleCartTransaction(Long productId, boolean purchaseSuccessful) {
    if (!purchaseSuccessful) {
        // Logic to delete the product or adjust stock
    }
}


Listener to react to the sent Product:

public void listenCartEvent(CartEvent event) {
        // Business logic to process the event
        addProductToUserCart(event.getUserId(), event.getProduct());
    }

    public void addProductToUserCart(Long userId, Product product) {
        // Add product to the user's cart
        // ...
    }