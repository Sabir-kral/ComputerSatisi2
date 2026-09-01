package az.computer.demo.Response;

import lombok.Data;

@Data
public class CartResponse {
    private Long id;
    private Integer quantity;
    private CartComputerResponse computer;
}