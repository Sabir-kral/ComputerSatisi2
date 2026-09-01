package az.computer.demo.Controller;

import az.computer.demo.Response.CartResponse;
import az.computer.demo.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/{computerId}")
    public ResponseEntity<String> addToCart(@PathVariable Long computerId, Principal principal) {
        cartService.addToCart(computerId, principal.getName());
        return ResponseEntity.ok("Məhsul səbətə əlavə edildi");
    }

    @GetMapping("/my-cart")
    public ResponseEntity<List<CartResponse>> getMyCart(Principal principal) {
        return ResponseEntity.ok(cartService.getUserCart(principal.getName()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestParam Long productId,
                                            @RequestParam Integer quantity,
                                            Principal principal) {
        cartService.updateQuantity(productId, quantity, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long productId,
                                            Principal principal) {
        cartService.removeItem(productId, principal.getName());
        return ResponseEntity.ok().build();
    }
}