package az.computer.demo.Repo;

import az.computer.demo.Entity.RoleEntity;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity,Long> {
    @Transactional
    @Modifying
    @Query(value = "insert into user_roles(user_id,role_id) select ?1,id from roles where customer = 1",nativeQuery = true)
    void assignCustomerRoles(Long userId);

    @Transactional
    @Modifying
    @Query(value = "insert into user_roles(user_id,role_id) select ?1,id from roles where admin = 1",nativeQuery = true)
    void assignAdminRoles(Long userId);

    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(Long userId);
}
