package az.computer.demo.Request;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private String name;
    private String surname;
    private String email;
}