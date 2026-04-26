package az.computer.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data // Getter, Setter və s. üçün (Lombok yoxdursa, əllə yazılmalıdır)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String address; // Müştərinin yazdığı tam ünvan

    private Long computerId;
    private Double amount; // Qəpiklə
    private String stripeChargeId; // Ödənişin referans kodu
    private String status; // Məsələn: "PAID", "SHIPPED", "DELIVERED"

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}