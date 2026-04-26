package az.computer.demo.Service;

import az.computer.demo.Entity.Order;
import az.computer.demo.Repo.OrderRepository;
import az.computer.demo.Request.OrderRequest;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    private OrderRepository orderRepository;

    public String createCharge(OrderRequest request) throws Exception {
        Stripe.apiKey = secretKey;

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", request.getAmount());
        chargeParams.put("currency", "usd"); // Və ya "azn"
        chargeParams.put("description", "Satınalma: " + request.getComputerId());
        chargeParams.put("source", request.getStripeToken());

        Charge charge = Charge.create(chargeParams);

        if (charge.getPaid()) {
            Order order = new Order();
            order.setEmail(request.getEmail());
            order.setAddress(request.getAddress());
            order.setComputerId(request.getComputerId());
            order.setAmount(request.getAmount());
            order.setStripeChargeId(charge.getId());
            order.setStatus("PAID");

            orderRepository.save(order);
            return charge.getId();
        }
        throw new RuntimeException("Ödəniş baş tutmadı.");
    }
}