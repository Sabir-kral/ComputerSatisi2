package az.computer.demo.Repo;

import az.computer.demo.Entity.CartEntity;
import az.computer.demo.Entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    List<CartEntity> findAllByUser(UserEntity user);

    Optional<CartEntity> findByUserAndComputer_Id(UserEntity user, Long computerId);

    Optional<CartEntity> findByComputer_IdAndUser_Email(Long computerId, String email);

    @Modifying
    @Transactional
    void deleteByComputer_IdAndUser_Email(Long computerId, String email);
}