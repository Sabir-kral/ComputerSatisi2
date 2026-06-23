package az.computer.demo.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String message;
    private Boolean isVerified;
}