package az.computer.demo.Request;

import lombok.Data;
import java.util.List;

@Data
public class ComputerRequest {
    private String name;
    private String description;
    private Double price;
    private List<String> imageLinks; // Frontend-dən gələn şəkil linkləri
}