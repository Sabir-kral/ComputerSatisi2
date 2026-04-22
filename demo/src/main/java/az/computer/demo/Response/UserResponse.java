package az.computer.demo.Response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;

    private String email;
    private String password;

}
