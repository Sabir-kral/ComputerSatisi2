package az.computer.demo.Response;

import lombok.Data;
import java.util.List;

@Data
public class CustomerResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private List<ComputerResponse> sellingComputers; // Satdığı kompüterlər
    private List<ComputerResponse> boughtComputers;  // Aldığı kompüterlər
}