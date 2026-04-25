package az.computer.demo.Service;

import az.computer.demo.Entity.EmailVerificationEntity;
import az.computer.demo.Entity.UserEntity;
import az.computer.demo.Exception.CustomException;
import az.computer.demo.Repo.EmailVerificationRepository;
import az.computer.demo.Repo.UserRepo;
import az.computer.demo.Request.AddVerifyRequest;
import az.computer.demo.Request.ResendOtpRequest;
import az.computer.demo.Response.MessageResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MailService mailService;
    private final LogService logService;

    public void isUserExists(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.getIsVerified())) {
                // Already verified → truly exists, block registration
                throw new CustomException("User already exists", "Bad Request", 400);
            } else {
                // Exists but not verified → delete old data and let them re-register
                emailVerificationRepository.findByEmail(email)
                        .ifPresent(emailVerificationRepository::delete);
                // We don't delete the user here; CustomerService will overwrite via save
                // But safest is to delete old unverified user so a fresh one is created
                userRepository.delete(user);
            }
        });
    }

    public UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public MessageResponse verify(AddVerifyRequest request) {
        // 1. Find OTP by email only first
        EmailVerificationEntity otp = emailVerificationRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(
                        "Bu email üçün OTP tapılmadı", "Not Found", 404));

        // 2. Check token matches — THIS is the fix for random code working
        if (!otp.getToken().equals(request.getCode())) {
            throw new CustomException("Yanlış OTP kodu", "Invalid OTP", 400);
        }

        // 3. Check expiry
        if (otp.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new CustomException("OTP vaxtı keçib", "OTP expired", 410);
        }

        // 4. Activate user
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found", "Not Found", 404));

        emailVerificationRepository.delete(otp);
        user.setIsVerified(true);
        userRepository.save(user);

        MessageResponse response = new MessageResponse();
        response.setMessage("verified you can now login");
        response.setIsVerified(true);
        logService.add("User verified with email: " + user.getEmail(), "USER_VERIFIED");

        return response;
    }

    public MessageResponse resendOtp(ResendOtpRequest request) throws MessagingException {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found", "Not Found", 404));

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            throw new CustomException("İstifadəçi artıq təsdiqlənib", "Already verified", 400);
        }

        emailVerificationRepository.findByEmail(request.getEmail())
                .ifPresent(emailVerificationRepository::delete);

        String code = generateCode();
        EmailVerificationEntity entity = new EmailVerificationEntity();
        entity.setEmail(request.getEmail());
        entity.setUser(user);
        entity.setToken(code);
        entity.setExpirationDate(LocalDateTime.now().plusMinutes(2));
        emailVerificationRepository.save(entity);

        mailService.verifyEmail(request.getEmail(), code);

        MessageResponse response = new MessageResponse();
        response.setMessage("OTP resend olundu");
        response.setIsVerified(false);
        logService.add("User resended otp with email: " + user.getEmail(), "USER_RESENDED_OTP");

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