package az.computer.demo.Service;

import az.computer.demo.Entity.EmailVerificationEntity;
import az.computer.demo.Entity.UserEntity;
import az.computer.demo.Entity.*;
import az.computer.demo.Exception.CustomException;
import az.computer.demo.Repo.EmailVerificationRepository;
import az.computer.demo.Repo.UserRepo;
import az.computer.demo.Request.AddVerifyRequest;
import az.computer.demo.Request.UserRequest;
import az.computer.demo.Request.ResendOtpRequest;
import az.computer.demo.Response.MessageResponse;
import az.computer.demo.Response.UserResponse;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MailService mailService;
    private final LogService logService;


    public void isUserExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException("User already exists", "Bad Request", 400);
        }
    }

    public UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }


    public MessageResponse verify(AddVerifyRequest request) {
        EmailVerificationEntity otp = emailVerificationRepository
                .findByEmailAndToken(request.getEmail(), request.getCode())
                .orElseThrow(() -> new CustomException("Yanlış OTP kodu", "Invalid OTP", 400));

        if (otp.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new CustomException("OTP vaxtı keçib", "OTP expired", 410);
        }

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found", "Not Found", 404));

        emailVerificationRepository.delete(otp);
        user.setIsVerified(true);
        userRepository.save(user);

        MessageResponse response = new MessageResponse();
        response.setMessage("verified you can now login");
        response.setIsVerified(true);
        logService.add("User verified with email: "+user.getEmail(),"USER_VERIFIED");

        return response;
    }

    public MessageResponse resendOtp(ResendOtpRequest request) throws MessagingException {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found", "Not Found", 404));

        // Köhnə kodları təmizlə ki, həm baza dolmasın, həm də 'unique' xətası olmasın
        emailVerificationRepository.findByEmail(request.getEmail()).ifPresent(emailVerificationRepository::delete);

        String code = generateCode();
        EmailVerificationEntity entity = new EmailVerificationEntity();
        entity.setEmail(request.getEmail());
        entity.setUser(user);
        entity.setToken(code);
        entity.setExpirationDate(LocalDateTime.now().plusMinutes(2)); // Mail-də 2 dəqiqə demisən, bura da 2 et
        emailVerificationRepository.save(entity);

        mailService.verifyEmail(request.getEmail(), code);
        // ... qalan kodlar


        MessageResponse response = new MessageResponse();
        response.setMessage("OTP resend olundu");
        response.setIsVerified(false);
        logService.add("User resended otb with email: "+user.getEmail(),"USER_RESENDED_OTB");


        return response;
    }

    private String generateCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }

}