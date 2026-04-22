package az.computer.demo.Repo;

import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo  extends JpaRepository<CustomerEntity,Long> {
    Optional<CustomerEntity> findByEmail(String email);
    Optional<CustomerEntity> findByUser(UserEntity userEntity);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users WHERE email = :email", nativeQuery = true)
    void deleteByEmailNative(@Param("email") String email);
}
