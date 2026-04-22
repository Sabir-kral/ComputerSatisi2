package az.computer.demo.Exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {

    String error;
    Integer status;

    public CustomException(String message, String error, Integer status) {
        super(message);
        this.error = error;
        this.status = status;
    }
}