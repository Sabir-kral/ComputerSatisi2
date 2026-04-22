package az.computer.demo.Request;

import lombok.Data;

@Data
public class CustomerRequest {
    private String name;
    private String surname;
    private String email;
    private String password;
}
