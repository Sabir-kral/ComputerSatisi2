package az.computer.demo.Request;

import lombok.Data;

@Data
public class AddVerifyRequest {

    private String email;
    private String code;
}