package az.computer.demo.Request;

import lombok.Data;

@Data
public class OrderRequest {
    private String email;
    private String address;
    private Long computerId;
    private Double amount; // Qəpiklə (məs: 1500 AZN = 150000)
    private String stripeToken;

    // Getter və Setter-lər
}