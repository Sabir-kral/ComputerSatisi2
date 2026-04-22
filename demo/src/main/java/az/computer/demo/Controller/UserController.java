package az.computer.demo.Controller;

import az.computer.demo.Request.AddVerifyRequest;
import az.computer.demo.Request.ResendOtpRequest;
import az.computer.demo.Request.UserRequest;
import az.computer.demo.Response.LoginResponse;
import az.computer.demo.Response.MessageResponse;
import az.computer.demo.Response.UserResponse;
import az.computer.demo.Service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private final UserService service;

    @PostMapping("/verify")
    public MessageResponse verify(@RequestBody AddVerifyRequest request) {
        return service.verify(request);
    }



    @PostMapping("/resendOTP")
    public MessageResponse resendOTP(@RequestBody ResendOtpRequest request) throws MessagingException {
        return service.resendOtp(request);
    }


}