import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cart.model.Cart;
import cart.repository.CartRepository;
import cart.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CartTest {

    // @Mock
    // private CartRepository cartRepository;

    // @InjectMocks
    // private CartService cartService;

    // @BeforeEach
    // void setUp() {
    //     MockitoAnnotations.openMocks(this);
    // }

    // @Test
    // void testIsImageAvailable() {
    //     Long imageId = 25L;
    //     Integer transactionCount = 5; // from TransactionService
    //     Integer countInCarts = 3; // from CartRepository

    //     when(cartRepository.checkImagesInCart(imageId)).thenReturn(countInCarts);

    //     boolean isAvailable = cartService.isImageAvailable(imageId, transactionCount);

    //     assertTrue(isAvailable);
    // }
}
