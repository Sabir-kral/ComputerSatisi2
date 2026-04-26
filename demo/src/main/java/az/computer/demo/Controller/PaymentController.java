package az.computer.demo.Controller;

import az.computer.demo.Request.OrderRequest;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {

    @PostMapping("/charge")
    public ResponseEntity<?> chargeCard(@RequestBody OrderRequest request) {
        // Stripe-ı konfiqurasiya et (Öz test key-ini bura yazacaqsan)
        Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", request.getAmount());
            params.put("currency", "usd"); // və ya "azn"
            params.put("description", "Computer Satışı: " + request.getComputerId());
            params.put("source", request.getStripeToken());

            Charge charge = Charge.create(params);

            // Ödəniş uğurludursa, burada bazaya (Order table) məlumatları yazmalısan
            // orderService.saveOrder(request, charge.getId());

            return ResponseEntity.ok(Map.of("status", "Success", "id", charge.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("status", "Error", "message", e.getMessage()));
        }
    }
}