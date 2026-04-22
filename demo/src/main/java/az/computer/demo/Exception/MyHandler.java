package az.computer.demo.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("error", ex.getError());
        map.put("status", ex.getStatus());

        // İndi brauzer bunu real xəta (məsələn 400) kimi görəcək
        return ResponseEntity.status(ex.getStatus()).body(map);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, Object> handleAccessDeniedException() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Giris icazesi yoxdur");
        map.put("error", "Forbidden");
        map.put("status", 403);

        return map;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> map = new HashMap<>();

        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);

        map.put("message", fieldError.getDefaultMessage());
        map.put("error", "Validation Error");
        map.put("status", 400);

        return map;
    }
}