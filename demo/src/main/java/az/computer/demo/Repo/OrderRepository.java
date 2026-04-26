package az.computer.demo.Repo;

import az.computer.demo.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Müəyyən bir email-ə aid sifarişləri tapmaq üçün (istəyə bağlı)
    List<Order> findByEmail(String email);

    // Statusa görə filtrləmək üçün (Satıcı paneli üçün)
    List<Order> findByStatus(String status);
}