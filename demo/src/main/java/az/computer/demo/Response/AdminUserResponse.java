package az.computer.demo.Response;

import lombok.Data;

@Data
public class AdminUserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Boolean banned;
}