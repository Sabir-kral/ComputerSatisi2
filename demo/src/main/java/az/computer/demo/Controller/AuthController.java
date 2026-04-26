package az.computer.demo.Controller;

import az.computer.demo.Entity.UserEntity;
import az.computer.demo.Repo.UserRepo;
import az.computer.demo.Request.LoginRequest;
import az.computer.demo.Response.LoginResponse;
import az.computer.demo.Service.CustomUserDetailsService;
import az.computer.demo.Utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepo userRepo;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Şifrə və ya email səhvdir");
        } catch (Exception e) {
            // Əgər email təsdiqlənməyibsə bura düşəcək
            throw new RuntimeException(e.getMessage());
        }

        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        UserEntity userEntity = userRepo.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("Not Found"));
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(userEntity.getId(),request.getEmail(),accessToken);
    }
}