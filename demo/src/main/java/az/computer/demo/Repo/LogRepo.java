package az.computer.demo.Repo;

import az.computer.demo.Entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepo extends JpaRepository<LogEntity,Long> {
}
