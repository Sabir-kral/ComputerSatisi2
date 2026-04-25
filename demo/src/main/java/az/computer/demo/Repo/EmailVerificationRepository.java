package az.computer.demo.Repo;

import az.computer.demo.Entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {

    // Used in verify() and resendOtp()
    Optional<EmailVerificationEntity> findByEmail(String email);

    // REMOVE findByEmailAndToken — we now check the token manually in service
}