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

public MessageResponse(String message) {
        this.message = message;
        this.isVerified = true; 
    }
}