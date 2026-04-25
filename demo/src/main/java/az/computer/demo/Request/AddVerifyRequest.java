package az.computer.demo.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddVerifyRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("code")
    private String code;
}