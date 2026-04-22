package az.computer.demo.Repo;

import az.computer.demo.Entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {

    Optional<EmailVerificationEntity> findByEmailAndToken(String email, String token);
    Optional<EmailVerificationEntity> findByEmail(String email);
    void deleteByEmail(String email);
}