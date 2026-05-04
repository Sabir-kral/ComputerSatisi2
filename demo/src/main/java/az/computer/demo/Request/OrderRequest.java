package az.computer.demo.Request;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private String email;      // Alıcının email-i
    private String phone;      // Alıcının telefon nömrəsi (Payment-dən gələn)
    private List<Long> computerIds; // Birdən çox kompüter üçün List
    private Double amount;     // Ümumi məbləğ
}