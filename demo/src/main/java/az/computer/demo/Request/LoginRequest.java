package az.computer.demo.Request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    private String email;


    private String password;

}
