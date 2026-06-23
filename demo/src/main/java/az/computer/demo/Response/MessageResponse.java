package az.computer.demo.Response;

import lombok.Data;

@Data
@NoArgsConstructer
@AllArgsConstructor
public class MessageResponse {

    private String message;
    private Boolean isVerified;
}